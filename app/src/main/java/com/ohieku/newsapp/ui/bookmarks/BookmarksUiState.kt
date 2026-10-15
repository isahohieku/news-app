package com.ohieku.newsapp.ui.bookmarks

import com.ohieku.newsapp.domain.features.news.model.Article

data class BookmarksUiState(
    val isLoading: Boolean = true,
    val bookmarks: List<Article> = emptyList(),
)
