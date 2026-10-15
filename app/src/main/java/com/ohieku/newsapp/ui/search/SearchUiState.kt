package com.ohieku.newsapp.ui.search

import com.ohieku.newsapp.ui.common.paging.PagedArticles

data class SearchUiState(
    val query: String = "",
    val results: PagedArticles = PagedArticles.Idle,
    val bookmarkedUrls: Set<String> = emptySet(),
)
