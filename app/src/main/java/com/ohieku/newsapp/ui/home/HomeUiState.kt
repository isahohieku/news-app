package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.domain.features.news.model.Article

data class HomeUiState(
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val articles: List<Article> = emptyList(),
    // Last page loaded successfully; 0 means nothing has loaded yet
    val currentPage: Int = 0
) {
    companion object {
        fun initial(): HomeUiState = HomeUiState()
    }
}
