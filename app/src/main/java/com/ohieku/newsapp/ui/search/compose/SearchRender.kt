package com.ohieku.newsapp.ui.search.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.extensions.utils.openInBrowser
import com.ohieku.newsapp.ui.search.SearchViewModel

@Composable
fun renderSearchScreen(
    onBackTapped: () -> Unit,
    onOpenSaved: () -> Unit,
    onBrowseCategory: (NewsCategory) -> Unit
) {
    val viewModel: SearchViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    SearchScreen(
        state = state,
        onQueryChange = viewModel::onQueryChange,
        onSearch = viewModel::search,
        onBackTapped = onBackTapped,
        onFetchNextPageTapped = viewModel::fetchNextPage,
        onRetryTapped = viewModel::retry,
        onArticleTapped = { url ->
            viewModel.onResultOpened()
            context.openInBrowser(url)
        },
        onSetBookmarked = viewModel::setBookmarked,
        onOpenSaved = onOpenSaved,
        onSortChange = viewModel::setSort,
        onRecentSearchTapped = viewModel::selectRecentSearch,
        onClearRecentSearches = viewModel::clearRecentSearches,
        onBrowseCategory = onBrowseCategory
    )
}
