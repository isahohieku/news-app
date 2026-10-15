package com.ohieku.newsapp.ui.search

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import com.ohieku.newsapp.domain.features.news.usecase.SearchNewsUseCase
import com.ohieku.newsapp.fakes.FakeBookmarkRepository
import com.ohieku.newsapp.ui.search.SearchViewModel.Companion.SEARCH_DEBOUNCE_MS
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SearchViewModelTest {

    private class FakeNewsRepository : NewsRepository {
        val searches = mutableListOf<Pair<String, Int>>()
        var resultsFor: (query: String) -> List<String> = { query -> listOf("$query 1", "$query 2") }

        override suspend fun getNewsHeadline(category: NewsCategory, page: Int): ResourceState<NewsModel> = error("Not used")

        override suspend fun searchNews(query: String, page: Int): ResourceState<NewsModel> {
            searches += query to page
            val titles = resultsFor(query)
            return ResourceState.Success(
                NewsModel("ok", titles.size, titles.map { Article(null, it, null, "https://example.com/$it", null, null, null, null) })
            )
        }
    }

    private val dispatcher = UnconfinedTestDispatcher()
    private val repository = FakeNewsRepository()
    private val viewModel by lazy { SearchViewModel(SearchNewsUseCase(repository), FakeBookmarkRepository()) }

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `nothing is searched before a query is typed`() = runTest(dispatcher) {
        advanceTimeBy(SEARCH_DEBOUNCE_MS * 2)

        assertTrue(viewModel.uiState.value.results.isIdle)
        assertTrue(repository.searches.isEmpty())
    }

    @Test
    fun `typing searches once the user pauses`() = runTest(dispatcher) {
        viewModel.onQueryChange("climate")
        advanceTimeBy(SEARCH_DEBOUNCE_MS - 1)

        assertTrue(repository.searches.isEmpty())

        advanceTimeBy(2)

        assertEquals(listOf("climate" to 1), repository.searches)
        assertEquals(listOf("climate 1", "climate 2"), viewModel.uiState.value.results.articles.map { it.title })
    }

    @Test
    fun `only the last of several quick changes is searched`() = runTest(dispatcher) {
        viewModel.onQueryChange("cl")
        advanceTimeBy(100)
        viewModel.onQueryChange("clim")
        advanceTimeBy(100)
        viewModel.onQueryChange("  climate  ")
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        assertEquals(listOf("climate" to 1), repository.searches)
    }

    @Test
    fun `one letter is not searched`() = runTest(dispatcher) {
        viewModel.onQueryChange("c")
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        assertTrue(repository.searches.isEmpty())
        assertTrue(viewModel.uiState.value.results.isIdle)
    }

    @Test
    fun `search key searches straight away without repeating after the pause`() = runTest(dispatcher) {
        viewModel.onQueryChange("climate")

        viewModel.search()
        assertEquals(listOf("climate" to 1), repository.searches)

        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)
        assertEquals(listOf("climate" to 1), repository.searches)
    }

    @Test
    fun `no matches shows an empty result rather than the idle hint`() = runTest(dispatcher) {
        repository.resultsFor = { emptyList() }

        viewModel.onQueryChange("zzqxqzz")
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        val results = viewModel.uiState.value.results
        assertFalse(results.isIdle)
        assertTrue(results.articles.isEmpty())
        assertTrue(results.endReached)
    }

    @Test
    fun `clearing the query goes back to the hint`() = runTest(dispatcher) {
        viewModel.onQueryChange("climate")
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        viewModel.onQueryChange("")
        advanceTimeBy(SEARCH_DEBOUNCE_MS + 1)

        assertTrue(viewModel.uiState.value.results.isIdle)
        assertTrue(viewModel.uiState.value.results.articles.isEmpty())
    }
}
