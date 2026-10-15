package com.ohieku.newsapp.ui.search

import com.ohieku.newsapp.domain.features.news.model.SearchSort
import com.ohieku.newsapp.ui.common.paging.PagedArticles

data class SearchUiState(
    val query: String = "",
    val results: PagedArticles = PagedArticles.Idle,
    val bookmarkedUrls: Set<String> = emptySet(),
    val sort: SearchSort = SearchSort.Relevance,
    val recentSearches: List<String> = emptyList(),
)
