package com.ohieku.newsapp.ui.bookmarks

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
        val viewModel = BookmarksViewModel(repository)

        assertFalse(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.bookmarks.isEmpty())

        repository.addBookmark(article("a"))

        assertEquals(listOf("a"), viewModel.uiState.value.bookmarks.map { it.title })
    }

    @Test
    fun `removing a bookmark drops it from the list`() = runTest {
        repository.addBookmark(article("a"))
        repository.addBookmark(article("b"))
        val viewModel = BookmarksViewModel(repository)

        viewModel.removeBookmark("https://example.com/a")

        assertEquals(listOf("b"), viewModel.uiState.value.bookmarks.map { it.title })
    }
}
