package com.ohieku.newsapp.ui.home

data class HomeUiState(
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val isError: Boolean = false,
    val articles: List<com.ohieku.newsapp.data.features.news.model.Article> = emptyList(),
    val currentPage: Int = 1
) {
    companion object {
        fun initial(): HomeUiState = HomeUiState()
    }
}
