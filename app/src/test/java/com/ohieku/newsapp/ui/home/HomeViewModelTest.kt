package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
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
        val results = ArrayDeque<ResourceState<NewsModel>>()

        override suspend fun getNewsHeadline(page: Int): ResourceState<NewsModel> {
            requestedPages += page
            return results.removeFirst()
        }
    }

    private val repository = FakeNewsRepository()

    private fun article(title: String) = Article(null, title, null, null, null, null, null, null)

    private fun success(vararg titles: String, total: Int = 100): ResourceState<NewsModel> =
        ResourceState.Success(NewsModel("ok", total, titles.map(::article)))

    private fun failure(message: String): ResourceState<NewsModel> =
        ResourceState.Error(ApiException(message))

    private fun createViewModel() = HomeViewModel(GetNewsUseCase(repository))

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

        val state = createViewModel().uiState.value

        assertEquals(listOf(1), repository.requestedPages)
        assertFalse(state.isLoading)
        assertNull(state.errorMessage)
        assertEquals(listOf("a", "b"), state.articles.map { it.title })
        assertEquals(1, state.currentPage)
    }

    @Test
    fun `first page failure stops loading and shows the error`() {
        repository.results += failure("No Internet, please check and try again")

        val state = createViewModel().uiState.value

        assertFalse(state.isLoading)
        assertEquals("No Internet, please check and try again", state.errorMessage)
        assertTrue(state.articles.isEmpty())
        assertEquals(0, state.currentPage)
    }

    @Test
    fun `retry after first page failure loads page one`() {
        repository.results += failure("No Internet, please check and try again")
        repository.results += success("a")
        val viewModel = createViewModel()

        viewModel.retry()

        val state = viewModel.uiState.value
        assertEquals(listOf(1, 1), repository.requestedPages)
        assertNull(state.errorMessage)
        assertEquals(listOf("a"), state.articles.map { it.title })
    }

    @Test
    fun `next page failure keeps articles and retry requests the same page`() {
        repository.results += success("a")
        repository.results += failure("Too many requests, try again later")
        repository.results += success("b")
        val viewModel = createViewModel()

        viewModel.fetchNextPage()

        viewModel.uiState.value.let { state ->
            assertEquals("Too many requests, try again later", state.errorMessage)
            assertEquals(listOf("a"), state.articles.map { it.title })
            assertEquals(1, state.currentPage)
            assertFalse(state.isLoading)
        }

        viewModel.retry()

        viewModel.uiState.value.let { state ->
            assertEquals(listOf(1, 2, 2), repository.requestedPages)
            assertNull(state.errorMessage)
            assertEquals(listOf("a", "b"), state.articles.map { it.title })
            assertEquals(2, state.currentPage)
        }
    }
}
