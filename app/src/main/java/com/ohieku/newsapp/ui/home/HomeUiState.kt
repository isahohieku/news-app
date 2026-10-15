package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.ui.common.paging.PagedArticles

data class HomeUiState(
    val category: NewsCategory = NewsCategory.Top,
    val feed: PagedArticles = PagedArticles(),
    val bookmarkedUrls: Set<String> = emptySet(),
) {
    companion object {
        fun initial(): HomeUiState = HomeUiState()
    }
}
