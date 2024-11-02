package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.domain.features.news.model.Article

data class HomeUiState(
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val articles: List<Article> = emptyList(),
    val currentPage: Int = 1
) {
    companion object {
        fun initial(): HomeUiState = HomeUiState()
    }
}
