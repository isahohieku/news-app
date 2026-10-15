package com.ohieku.newsapp.ui.common.paging

import com.ohieku.newsapp.domain.common.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article

data class PagedArticles(
    val totalResults: Int = 0,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val error: ErrorType? = null,
    // A failed pull-to-refresh keeps the current list and reports the error once
    val refreshError: ErrorType? = null,
    val articles: List<Article> = emptyList(),
    // Last page loaded successfully; 0 means nothing has loaded yet
    val currentPage: Int = 0,
    // NewsAPI pages hold fewer articles than totalResults suggests, so an empty page marks the end
    val endReached: Boolean = false,
    // When the first page last arrived; shown as "as of" so day-old headlines are admitted plainly
    val loadedAt: Long? = null
) {
    val hasMorePages: Boolean
        get() = !endReached && (currentPage == 0 || articles.size < totalResults)

    /** Nothing requested yet (e.g. a search before the first query). */
    val isIdle: Boolean
        get() = currentPage == 0 && !isLoading && error == null

    companion object {
        val Idle = PagedArticles(isLoading = false)
    }
}
