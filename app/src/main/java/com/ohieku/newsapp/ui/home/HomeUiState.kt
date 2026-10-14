package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article

data class HomeUiState(
    val totalResults: Int = 0,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: ErrorType? = null,
    val articles: List<Article> = emptyList(),
    // Last page loaded successfully; 0 means nothing has loaded yet
    val currentPage: Int = 0,
    // NewsAPI pages hold fewer articles than totalResults suggests, so an empty page marks the end
    val endReached: Boolean = false
) {
    val hasMorePages: Boolean
        get() = !endReached && (currentPage == 0 || articles.size < totalResults)

    companion object {
        fun initial(): HomeUiState = HomeUiState()
    }
}
