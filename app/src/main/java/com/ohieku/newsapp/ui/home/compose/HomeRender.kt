package com.ohieku.newsapp.ui.home.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.extensions.utils.openInBrowser
import com.ohieku.newsapp.ui.home.HomeViewModel

@Composable
fun renderHomeScreen(
    onSearchTapped: () -> Unit,
    onOpenSaved: () -> Unit,
    scrollToTopSignal: Int,
    requestedCategory: NewsCategory? = null,
    onRequestedCategoryHandled: () -> Unit = {}
) {
    val viewModel: HomeViewModel = hiltViewModel()
    // A category picked elsewhere (Search's "Browse a category") arrives here once
    LaunchedEffect(requestedCategory) {
        if (requestedCategory != null) {
            viewModel.selectCategory(requestedCategory)
            onRequestedCategoryHandled()
        }
    }
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    HomeScreen(
        state = state,
        onFetchNextPageTapped = viewModel::fetchNextPage,
        onRetryTapped = viewModel::retry,
        onArticleTapped = { url -> context.openInBrowser(url) },
        onSetBookmarked = viewModel::setBookmarked,
        onCategorySelected = viewModel::selectCategory,
        onRefresh = viewModel::refresh,
        onRefreshErrorShown = viewModel::refreshErrorShown,
        onSearchTapped = onSearchTapped,
        onOpenSaved = onOpenSaved,
        scrollToTopSignal = scrollToTopSignal
    )
}
