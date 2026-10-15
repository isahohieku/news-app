package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.common.paging.PagedArticles

private const val LOAD_MORE_THRESHOLD = 5

/**
 * A paged list of article cards with loading, error and empty states.
 * [header] scrolls with the list and stays on screen in the other states; [onRefresh] enables pull-to-refresh.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleFeed(
    feed: PagedArticles,
    bookmarkedUrls: Set<String>,
    onFetchNextPage: () -> Unit,
    onRetry: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onBookmarkTapped: (Article) -> Unit,
    modifier: Modifier = Modifier,
    onRefresh: (() -> Unit)? = null,
    header: (@Composable () -> Unit)? = null,
    emptyContent: @Composable () -> Unit = { EmptyState() },
) {
    if (feed.articles.isNotEmpty() && !feed.isLoading) {
        val listState = rememberLazyListState()
        val isNearEnd by remember {
            derivedStateOf {
                val layoutInfo = listState.layoutInfo
                val lastVisibleIndex = layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
                lastVisibleIndex >= layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
            }
        }

        // Keyed on the page so a short page that keeps the end in view still loads the next one
        LaunchedEffect(isNearEnd, feed.currentPage) {
            if (isNearEnd) onFetchNextPage()
        }

        val pullToRefreshState = rememberPullToRefreshState(enabled = { onRefresh != null })
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(Unit) { onRefresh?.invoke() }
        }
        LaunchedEffect(feed.isRefreshing) {
            if (feed.isRefreshing) pullToRefreshState.startRefresh() else pullToRefreshState.endRefresh()
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                // The resting indicator sits above the list; clip it so it doesn't draw over the app bar
                .clipToBounds()
                .nestedScroll(pullToRefreshState.nestedScrollConnection)
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                if (header != null) {
                    item {
                        header()
                    }
                }

                itemsIndexed(
                    items = feed.articles,
                    key = { index, article -> article.url ?: "no-url-$index" }
                ) { _, article ->
                    NewsCardComponent(
                        article = article,
                        onClick = article.url?.let { url -> { onArticleTapped(url) } },
                        isBookmarked = article.url in bookmarkedUrls,
                        onBookmarkClick = article.url?.let { { onBookmarkTapped(article) } }
                    )
                }

                if (feed.isLoadingMore) {
                    item {
                        LoadMoreIndicator()
                    }
                } else if (feed.error != null) {
                    item {
                        PageErrorFooter(stringResource(feed.error.messageRes()), onRetry)
                    }
                }
            }

            if (onRefresh != null) {
                PullToRefreshContainer(
                    state = pullToRefreshState,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.align(Alignment.TopCenter)
                )
            }
        }
    } else {
        // Keep the header on screen so, e.g., an empty category can be switched away from
        Column(modifier = modifier.fillMaxSize()) {
            header?.invoke()

            Box(modifier = Modifier.fillMaxWidth().weight(1f)) {
                when {
                    feed.isLoading -> Loader()
                    feed.error != null -> ErrorState(stringResource(feed.error.messageRes()), onRetry)
                    else -> emptyContent()
                }
            }
        }
    }
}
