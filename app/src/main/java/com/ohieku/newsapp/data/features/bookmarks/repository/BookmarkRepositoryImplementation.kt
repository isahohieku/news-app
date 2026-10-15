package com.ohieku.newsapp.data.features.bookmarks.repository

import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkDao
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkEntity
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.Source
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImplementation @Inject constructor(
    private val bookmarkDao: BookmarkDao,
) : BookmarkRepository {

    override fun observeBookmarks(): Flow<List<Article>> =
        bookmarkDao.observeAll().map { bookmarks -> bookmarks.map { it.toArticle() } }

    override fun observeBookmarkedUrls(): Flow<Set<String>> =
        bookmarkDao.observeUrls().map { it.toSet() }

    override suspend fun addBookmark(article: Article) {
        val url = article.url ?: return
        bookmarkDao.insert(article.toEntity(url, savedAt = System.currentTimeMillis()))
    }

    override suspend fun removeBookmark(url: String) {
        bookmarkDao.delete(url)
    }

    private fun Article.toEntity(url: String, savedAt: Long) = BookmarkEntity(
        url = url,
        title = title,
        description = description,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        author = author,
        sourceId = source?.id,
        sourceName = source?.name,
        savedAt = savedAt,
    )

    private fun BookmarkEntity.toArticle() = Article(
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content,
        source = if (sourceId == null && sourceName == null) null else Source(id = sourceId, name = sourceName),
    )
}
