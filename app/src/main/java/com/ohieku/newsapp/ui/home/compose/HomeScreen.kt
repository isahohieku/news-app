package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.home.HomeUiState
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.OverviewSection

private const val LOAD_MORE_THRESHOLD = 5

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit
) {
    MainScaffold { paddingValues ->
        when {
            state.isLoading -> Loader()

            state.articles.isNotEmpty() -> {
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

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        OverviewSection()
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
            }

            state.error != null -> ErrorState(stringResource(state.error.messageRes()), onRetryTapped)

            else -> EmptyState()
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
        onArticleTapped = { /* no-op */ }
    )
}
