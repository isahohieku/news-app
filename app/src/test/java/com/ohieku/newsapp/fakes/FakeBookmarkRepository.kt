package com.ohieku.newsapp.fakes

import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map

class FakeBookmarkRepository : BookmarkRepository {
    val bookmarks = MutableStateFlow<List<Article>>(emptyList())

    override fun observeBookmarks(): Flow<List<Article>> = bookmarks

    override fun observeBookmarkedUrls(): Flow<Set<String>> =
        bookmarks.map { articles -> articles.mapNotNull { it.url }.toSet() }

    override suspend fun addBookmark(article: Article) {
        if (article.url == null) return
        bookmarks.value = listOf(article) + bookmarks.value.filterNot { it.url == article.url }
    }

    override suspend fun removeBookmark(url: String) {
        bookmarks.value = bookmarks.value.filterNot { it.url == url }
    }
}
