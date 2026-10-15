package com.ohieku.newsapp.data.features.bookmarks.repository

import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkDao
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkEntity
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BookmarkRepositoryImplementationTest {

    private class FakeBookmarkDao : BookmarkDao {
        val rows = MutableStateFlow<List<BookmarkEntity>>(emptyList())

        override fun observeAll(): Flow<List<BookmarkEntity>> = rows.map { it.sortedByDescending(BookmarkEntity::savedAt) }

        override fun observeUrls(): Flow<List<String>> = rows.map { list -> list.map { it.url } }

        override suspend fun insert(bookmark: BookmarkEntity) {
            rows.value = rows.value.filterNot { it.url == bookmark.url } + bookmark
        }

        override suspend fun delete(url: String) {
            rows.value = rows.value.filterNot { it.url == url }
        }
    }

    private val dao = FakeBookmarkDao()
    private val repository = BookmarkRepositoryImplementation(dao)

    private val article = Article(
        author = "Author",
        title = "Title",
        description = "Description",
        url = "https://example.com/a",
        urlToImage = "https://example.com/a.jpg",
        publishedAt = "2026-10-13T19:06:00Z",
        content = "Content",
        source = Source(id = "id", name = "Source")
    )

    @Test
    fun `saved article comes back unchanged`() = runTest {
        repository.addBookmark(article)

        assertEquals(listOf(article), repository.observeBookmarks().first())
        assertEquals(setOf(article.url), repository.observeBookmarkedUrls().first())
    }

    @Test
    fun `article without a source keeps a null source`() = runTest {
        val noSource = article.copy(source = null)

        repository.addBookmark(noSource)

        assertEquals(listOf(noSource), repository.observeBookmarks().first())
    }

    @Test
    fun `article without a url is ignored`() = runTest {
        repository.addBookmark(article.copy(url = null))

        assertTrue(repository.observeBookmarks().first().isEmpty())
    }

    @Test
    fun `removing deletes by url`() = runTest {
        repository.addBookmark(article)

        repository.removeBookmark("https://example.com/a")

        assertTrue(repository.observeBookmarks().first().isEmpty())
    }
}
