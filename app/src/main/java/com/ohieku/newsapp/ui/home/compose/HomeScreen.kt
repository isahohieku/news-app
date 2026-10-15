package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.home.HomeUiState
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.OverviewSection

private const val LOAD_MORE_THRESHOLD = 5

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onCategorySelected: (NewsCategory) -> Unit,
    onRefresh: () -> Unit,
    onRefreshErrorShown: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val refreshErrorMessage = state.refreshError?.let { stringResource(it.messageRes()) }

    LaunchedEffect(state.refreshError) {
        if (refreshErrorMessage != null) {
            snackbarHostState.showSnackbar(refreshErrorMessage)
            onRefreshErrorShown()
        }
    }

    MainScaffold(snackbarHostState = snackbarHostState) { paddingValues ->
        val categories: @Composable () -> Unit = {
            OverviewSection(
                selectedCategory = state.category,
                onCategorySelected = onCategorySelected
            )
        }

        if (state.articles.isNotEmpty() && !state.isLoading) {
            val listState = rememberLazyListState()
            val isNearEnd by remember {
                derivedStateOf {
                    val layoutInfo = listState.layoutInfo
                    val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                    lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
                }
            }

            // Keyed on the page so a short page that keeps the end in view still loads the next one
            LaunchedEffect(isNearEnd, state.currentPage) {
                if (isNearEnd) onFetchNextPageTapped()
            }

            val pullToRefreshState = rememberPullToRefreshState()
            if (pullToRefreshState.isRefreshing) {
                LaunchedEffect(Unit) { onRefresh() }
            }
            LaunchedEffect(state.isRefreshing) {
                if (state.isRefreshing) pullToRefreshState.startRefresh() else pullToRefreshState.endRefresh()
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    // The resting indicator sits above the list; clip it so it doesn't draw over the app bar
                    .clipToBounds()
                    .nestedScroll(pullToRefreshState.nestedScrollConnection)
            ) {
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        categories()
                    }

                    itemsIndexed(
                        items = state.articles,
                        key = { index, article -> article.url ?: "no-url-$index" }
                    ) { _, article ->
                        NewsCardComponent(
                            article = article,
                            onClick = article.url?.let { url -> { onArticleTapped(url) } }
                        )
                    }

                    if (state.isLoadingMore) {
                        item {
                            LoadMoreIndicator()
                        }
                    } else if (state.error != null) {
                        item {
                            PageErrorFooter(stringResource(state.error.messageRes()), onRetryTapped)
                        }
                    }
                }

                PullToRefreshContainer(
                    state = pullToRefreshState,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        } else {
            // Keep the categories on screen so an empty or failed category can be switched away from
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                categories()

                Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    when {
                        state.isLoading -> Loader()
                        state.error != null -> ErrorState(stringResource(state.error.messageRes()), onRetryTapped)
                        else -> EmptyState()
                    }
                }
            }
        }
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
        onCategorySelected = { /* no-op */ },
        onRefresh = { /* no-op */ },
        onRefreshErrorShown = { /* no-op */ }
    )
}
