package com.ohieku.newsapp.ui.home.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ohieku.newsapp.extensions.utils.openInBrowser
import com.ohieku.newsapp.ui.home.HomeViewModel

@Composable
fun renderHomeScreen(onSearchTapped: () -> Unit) {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    HomeScreen(
        state = state,
        onFetchNextPageTapped = viewModel::fetchNextPage,
        onRetryTapped = viewModel::retry,
        onArticleTapped = { url -> context.openInBrowser(url) },
        onBookmarkTapped = viewModel::toggleBookmark,
        onCategorySelected = viewModel::selectCategory,
        onRefresh = viewModel::refresh,
        onRefreshErrorShown = viewModel::refreshErrorShown,
        onSearchTapped = onSearchTapped
    )
}
