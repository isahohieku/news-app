package com.ohieku.newsapp.domain.features.bookmarks.repository

import com.ohieku.newsapp.domain.features.news.model.Article
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    /** Saved articles, most recently saved first. */
    fun observeBookmarks(): Flow<List<Article>>

    fun observeBookmarkedUrls(): Flow<Set<String>>

    /** Articles without a URL can't be bookmarked and are ignored. */
    suspend fun addBookmark(article: Article)

    suspend fun removeBookmark(url: String)
}
