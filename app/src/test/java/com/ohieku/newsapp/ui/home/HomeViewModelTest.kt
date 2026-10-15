package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.model.SearchSort
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
import com.ohieku.newsapp.fakes.FakeBookmarkRepository
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private class FakeNewsRepository : NewsRepository {
        val requestedPages = mutableListOf<Int>()
        val requestedCategories = mutableListOf<NewsCategory>()
        val results = ArrayDeque<ResourceState<NewsModel>>()
        var gate: CompletableDeferred<Unit>? = null

        override suspend fun getNewsHeadline(category: NewsCategory, page: Int): ResourceState<NewsModel> {
            requestedPages += page
            requestedCategories += category
            gate?.await()
            return results.removeFirst()
        }

        override suspend fun searchNews(query: String, sort: SearchSort, page: Int): ResourceState<NewsModel> = error("Not used")
    }

    private val repository = FakeNewsRepository()

    private fun article(title: String) = Article(null, title, null, "https://example.com/$title", null, null, null, null)

    private fun success(vararg titles: String, total: Int = 100): ResourceState<NewsModel> =
        ResourceState.Success(NewsModel("ok", total, titles.map(::article)))

    private fun failure(type: ErrorType): ResourceState<NewsModel> =
        ResourceState.Error(ApiException(type))

    private val bookmarks = FakeBookmarkRepository()

    private fun createViewModel() = HomeViewModel(GetNewsUseCase(repository), bookmarks)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `first page loads on start`() {
        repository.results += success("a", "b")

        val state = createViewModel().uiState.value.feed

        assertEquals(listOf(1), repository.requestedPages)
        assertFalse(state.isLoading)
        assertNull(state.error)
        assertEquals(listOf("a", "b"), state.articles.map { it.title })
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `first page failure stops loading and shows the error`() {
        repository.results += failure(ErrorType.NoInternet)

        val state = createViewModel().uiState.value.feed

        assertFalse(state.isLoading)
        assertEquals(ErrorType.NoInternet, state.error)
        assertTrue(state.articles.isEmpty())
        assertEquals(0, state.currentPage)
    }

    @Test
    fun `retry after first page failure loads page one`() {
        repository.results += failure(ErrorType.NoInternet)
        repository.results += success("a")
        val viewModel = createViewModel()

        viewModel.retry()

        val state = viewModel.uiState.value.feed
        assertEquals(listOf(1, 1), repository.requestedPages)
        assertNull(state.error)
        assertEquals(listOf("a"), state.articles.map { it.title })
    }

    @Test
    fun `next page failure keeps articles and retry requests the same page`() {
        repository.results += success("a")
        repository.results += failure(ErrorType.RateLimited)
        repository.results += success("b")
        val viewModel = createViewModel()

        viewModel.fetchNextPage()

        viewModel.uiState.value.feed.let { state ->
            assertEquals(ErrorType.RateLimited, state.error)
            assertEquals(listOf("a"), state.articles.map { it.title })
            assertEquals(1, state.currentPage)
            assertFalse(state.isLoading)
        }

        viewModel.retry()

        viewModel.uiState.value.feed.let { state ->
            assertEquals(listOf(1, 2, 2), repository.requestedPages)
            assertNull(state.error)
            assertEquals(listOf("a", "b"), state.articles.map { it.title })
            assertEquals(2, state.currentPage)
        }
    }

    @Test
    fun `next page is not requested while one is loading`() {
        repository.results += success("a")
        repository.results += success("b")
        val viewModel = createViewModel()
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate

        viewModel.fetchNextPage()
        viewModel.fetchNextPage()
        viewModel.retry()

        assertTrue(viewModel.uiState.value.feed.isLoadingMore)
        assertEquals(listOf(1, 2), repository.requestedPages)

        gate.complete(Unit)

        viewModel.uiState.value.feed.let { state ->
            assertFalse(state.isLoadingMore)
            assertEquals(listOf("a", "b"), state.articles.map { it.title })
        }
    }

    @Test
    fun `empty page ends pagination`() {
        repository.results += success("a")
        repository.results += success(total = 0)
        val viewModel = createViewModel()

        viewModel.fetchNextPage()
        viewModel.fetchNextPage()

        val state = viewModel.uiState.value.feed
        assertEquals(listOf(1, 2), repository.requestedPages)
        assertTrue(state.endReached)
        assertFalse(state.hasMorePages)
        assertEquals(100, state.totalResults)
        assertEquals(listOf("a"), state.articles.map { it.title })
    }

    @Test
    fun `no request once every result is loaded`() {
        repository.results += success("a", "b", total = 2)
        val viewModel = createViewModel()

        viewModel.fetchNextPage()

        assertEquals(listOf(1), repository.requestedPages)
    }

    @Test
    fun `scrolling does not refetch while an error is showing`() {
        repository.results += success("a")
        repository.results += failure(ErrorType.RateLimited)
        val viewModel = createViewModel()

        viewModel.fetchNextPage()
        viewModel.fetchNextPage()

        assertEquals(listOf(1, 2), repository.requestedPages)
    }

    @Test
    fun `empty first page shows the empty state`() {
        repository.results += success(total = 0)

        val state = createViewModel().uiState.value.feed

        assertFalse(state.isLoading)
        assertNull(state.error)
        assertTrue(state.articles.isEmpty())
        assertTrue(state.endReached)
    }

    @Test
    fun `articles repeated on a later page are dropped`() {
        repository.results += success("a", "b")
        repository.results += success("b", "c", "c")
        val viewModel = createViewModel()

        viewModel.fetchNextPage()

        assertEquals(listOf("a", "b", "c"), viewModel.uiState.value.feed.articles.map { it.title })
    }

    @Test
    fun `unexpected exception type maps to unknown error`() {
        repository.results += ResourceState.Error(IllegalStateException("boom"))

        assertEquals(ErrorType.Unknown, createViewModel().uiState.value.feed.error)
    }

    @Test
    fun `selecting a category starts again from its first page`() {
        repository.results += success("a")
        repository.results += success("b")
        repository.results += success("x")
        val viewModel = createViewModel()
        viewModel.fetchNextPage()

        viewModel.selectCategory(NewsCategory.Sports)

        val state = viewModel.uiState.value.feed
        assertEquals(listOf(1, 2, 1), repository.requestedPages)
        assertEquals(NewsCategory.Sports, repository.requestedCategories.last())
        assertEquals(NewsCategory.Sports, viewModel.uiState.value.category)
        assertEquals(listOf("x"), state.articles.map { it.title })
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `selecting the current category does nothing`() {
        repository.results += success("a")
        val viewModel = createViewModel()

        viewModel.selectCategory(NewsCategory.Top)

        assertEquals(listOf(1), repository.requestedPages)
    }

    @Test
    fun `switching category while loading drops the old request`() {
        val gate = CompletableDeferred<Unit>()
        repository.gate = gate
        repository.results += success("science")
        repository.results += success("old")
        val viewModel = createViewModel()

        repository.gate = null
        viewModel.selectCategory(NewsCategory.Science)
        gate.complete(Unit)

        val state = viewModel.uiState.value.feed
        assertEquals(NewsCategory.Science, viewModel.uiState.value.category)
        assertEquals(listOf("science"), state.articles.map { it.title })
    }

    @Test
    fun `refresh replaces the list with the first page`() {
        repository.results += success("a")
        repository.results += success("b")
        repository.results += success("new")
        val viewModel = createViewModel()
        viewModel.fetchNextPage()

        viewModel.refresh()

        val state = viewModel.uiState.value.feed
        assertEquals(listOf(1, 2, 1), repository.requestedPages)
        assertFalse(state.isRefreshing)
        assertEquals(listOf("new"), state.articles.map { it.title })
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `failed refresh keeps the list and reports the error once`() {
        repository.results += success("a")
        repository.results += failure(ErrorType.NoInternet)
        val viewModel = createViewModel()

        viewModel.refresh()

        viewModel.uiState.value.feed.let { state ->
            assertFalse(state.isRefreshing)
            assertNull(state.error)
            assertEquals(ErrorType.NoInternet, state.refreshError)
            assertEquals(listOf("a"), state.articles.map { it.title })
        }

        viewModel.refreshErrorShown()

        assertNull(viewModel.uiState.value.feed.refreshError)
    }

    @Test
    fun `bookmarking an article saves it and marks it as saved`() {
        repository.results += success("a", "b")
        val viewModel = createViewModel()
        val article = viewModel.uiState.value.feed.articles.first()

        viewModel.toggleBookmark(article)

        assertEquals(setOf(article.url), viewModel.uiState.value.bookmarkedUrls)
        assertEquals(listOf(article), bookmarks.bookmarks.value)
    }

    @Test
    fun `bookmarking a saved article removes it`() {
        repository.results += success("a")
        val viewModel = createViewModel()
        val article = viewModel.uiState.value.feed.articles.first()
        viewModel.toggleBookmark(article)

        viewModel.toggleBookmark(article)

        assertTrue(viewModel.uiState.value.bookmarkedUrls.isEmpty())
        assertTrue(bookmarks.bookmarks.value.isEmpty())
    }

    @Test
    fun `the first page records when the bulletin was fetched`() {
        repository.results += success("a")
        repository.results += success("b")
        val viewModel = createViewModel()
        val firstLoad = viewModel.uiState.value.feed.loadedAt

        assertTrue(firstLoad != null)

        viewModel.fetchNextPage()

        // Later pages extend the same bulletin, so its "as of" time stays put
        assertEquals(firstLoad, viewModel.uiState.value.feed.loadedAt)
    }
}
