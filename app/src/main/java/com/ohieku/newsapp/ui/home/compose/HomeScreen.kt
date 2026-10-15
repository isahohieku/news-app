package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.OverviewSection
import com.ohieku.newsapp.ui.home.HomeUiState

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onBookmarkTapped: (Article) -> Unit,
    onCategorySelected: (NewsCategory) -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorShown: () -> Unit,
    onSearchTapped: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshErrorMessage = state.feed.refreshError?.let { stringResource(it.messageRes()) }

    LaunchedEffect(state.feed.refreshError) {
        if (refreshErrorMessage != null) {
            snackbarHostState.showSnackbar(refreshErrorMessage)
            onRefreshErrorShown()
        }
    }

    MainScaffold(
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = onSearchTapped) {
                Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search))
            }
        }
    ) { paddingValues ->
        ArticleFeed(
            feed = state.feed,
            bookmarkedUrls = state.bookmarkedUrls,
            onFetchNextPage = onFetchNextPageTapped,
            onRetry = onRetryTapped,
            onArticleTapped = onArticleTapped,
            onBookmarkTapped = onBookmarkTapped,
            onRefresh = onRefresh,
            modifier = Modifier.padding(paddingValues),
            header = {
                OverviewSection(
                    selectedCategory = state.category,
                    onCategorySelected = onCategorySelected
                )
            }
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        state = HomeUiState.initial(),
        onFetchNextPageTapped = { /* no-op */ },
        onRetryTapped = { /* no-op */ },
        onArticleTapped = { /* no-op */ },
        onBookmarkTapped = { /* no-op */ },
        onCategorySelected = { /* no-op */ },
        onRefresh = { /* no-op */ },
        onRefreshErrorShown = { /* no-op */ },
        onSearchTapped = { /* no-op */ }
    )
}
