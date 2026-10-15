package com.ohieku.newsapp.ui.bookmarks

import com.ohieku.newsapp.domain.features.bookmarks.usecase.ObserveBookmarksUseCase
import com.ohieku.newsapp.domain.features.bookmarks.usecase.SetBookmarkedUseCase
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.fakes.FakeBookmarkRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
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
class BookmarksViewModelTest {

    private val repository = FakeBookmarkRepository()

    private fun article(title: String) = Article(null, title, null, "https://example.com/$title", null, null, null, null)

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `shows saved articles and updates when they change`() = runTest {
        val viewModel = BookmarksViewModel(ObserveBookmarksUseCase(repository), SetBookmarkedUseCase(repository))

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.bookmarks.isEmpty())

        repository.addBookmark(article("a"))

        assertEquals(listOf("a"), viewModel.uiState.value.bookmarks.map { it.title })
    }

    @Test
    fun `removing a bookmark drops it from the list`() = runTest {
        repository.addBookmark(article("a"))
        repository.addBookmark(article("b"))
        val viewModel = BookmarksViewModel(ObserveBookmarksUseCase(repository), SetBookmarkedUseCase(repository))

        viewModel.setBookmarked(article("a"), saved = false)

        assertEquals(listOf("b"), viewModel.uiState.value.bookmarks.map { it.title })
    }

    @Test
    fun `undo puts a removed bookmark back`() = runTest {
        repository.addBookmark(article("a"))
        val viewModel = BookmarksViewModel(ObserveBookmarksUseCase(repository), SetBookmarkedUseCase(repository))
        viewModel.setBookmarked(article("a"), saved = false)

        viewModel.setBookmarked(article("a"), saved = true)

        assertEquals(listOf("a"), viewModel.uiState.value.bookmarks.map { it.title })
    }

    @Test
    fun `restoring explicitly works even before the removal has reached the list`() = runTest {
        repository.addBookmark(article("a"))
        val viewModel = BookmarksViewModel(ObserveBookmarksUseCase(repository), SetBookmarkedUseCase(repository))

        viewModel.setBookmarked(article("a"), saved = false)
        viewModel.setBookmarked(article("a"), saved = true)
        viewModel.setBookmarked(article("a"), saved = true)

        assertEquals(listOf("a"), viewModel.uiState.value.bookmarks.map { it.title })
    }
}
