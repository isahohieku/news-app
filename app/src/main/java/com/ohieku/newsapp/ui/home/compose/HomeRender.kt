package com.ohieku.newsapp.ui.home.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohieku.newsapp.extensions.utils.openInBrowser
import com.ohieku.newsapp.ui.home.HomeViewModel

@Composable
fun renderHomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    HomeScreen(
        state = state,
        onFetchNextPageTapped = viewModel::fetchNextPage,
        onRetryTapped = viewModel::retry,
        onArticleTapped = { url -> context.openInBrowser(url) }
    )
}