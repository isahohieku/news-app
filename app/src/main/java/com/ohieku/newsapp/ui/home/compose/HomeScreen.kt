package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.common.rememberBookmarkFeedback
import com.ohieku.newsapp.ui.compose.components.CategoryTabs
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.home.HomeUiState

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onSetBookmarked: (article: Article, saved: Boolean) -> Unit,
    onCategorySelected: (NewsCategory) -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorShown: () -> Unit,
    onSearchTapped: () -> Unit,
    onOpenSaved: () -> Unit,
    scrollToTopSignal: Int = 0
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshErrorMessage = state.feed.refreshError?.let { stringResource(it.messageRes()) }
    // Each category starts at its own top rather than inheriting the previous one's scroll position
    val listState = rememberSaveable(state.category, saver = LazyListState.Saver) { LazyListState() }
    val onBookmarkTapped = rememberBookmarkFeedback(
        snackbarHostState = snackbarHostState,
        bookmarkedUrls = state.bookmarkedUrls,
        onSetBookmarked = onSetBookmarked,
        onViewSaved = onOpenSaved
    )

    LaunchedEffect(state.feed.refreshError) {
        if (refreshErrorMessage != null) {
            snackbarHostState.showSnackbar(refreshErrorMessage)
            onRefreshErrorShown()
        }
    }

    // Tapping Home while already on Home returns to the lead story
    LaunchedEffect(scrollToTopSignal) {
        if (scrollToTopSignal > 0) listState.animateScrollToItem(0)
    }

    MainScaffold(
        snackbarHostState = snackbarHostState,
        actions = {
            IconButton(onClick = onSearchTapped) {
                Icon(Icons.Filled.Search, contentDescription = stringResource(R.string.search))
            }
        },
        tabs = {
            CategoryTabs(selectedCategory = state.category, onCategorySelected = onCategorySelected)
        }
    ) { paddingValues ->
        ArticleFeed(
            feed = state.feed,
            bookmarkedUrls = state.bookmarkedUrls,
            onFetchNextPage = onFetchNextPageTapped,
            onRetry = onRetryTapped,
            onArticleTapped = onArticleTapped,
            onBookmarkTapped = onBookmarkTapped,
            listState = listState,
            onRefresh = onRefresh,
            onOpenSaved = onOpenSaved,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    NewsAppTheme {
        HomeScreen(
            state = HomeUiState.initial(),
            onFetchNextPageTapped = { /* no-op */ },
            onRetryTapped = { /* no-op */ },
            onArticleTapped = { /* no-op */ },
            onSetBookmarked = { _, _ -> /* no-op */ },
            onCategorySelected = { /* no-op */ },
            onRefresh = { /* no-op */ },
            onRefreshErrorShown = { /* no-op */ },
            onSearchTapped = { /* no-op */ },
            onOpenSaved = { /* no-op */ }
        )
    }
}
