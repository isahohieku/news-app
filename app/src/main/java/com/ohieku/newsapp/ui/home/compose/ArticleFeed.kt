package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Newspaper
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
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.R
import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.ui.common.bodyRes
import com.ohieku.newsapp.ui.common.isConnectivity
import com.ohieku.newsapp.ui.common.messageRes
import com.ohieku.newsapp.ui.common.paging.PagedArticles
import com.ohieku.newsapp.extensions.utils.StoryDay
import com.ohieku.newsapp.extensions.utils.parsePublishedAt
import com.ohieku.newsapp.extensions.utils.storyDay

private const val LOAD_MORE_THRESHOLD = 5

/**
 * The bulletin: an optional lead story, then the running order, with loading, error and empty states.
 * [onRefresh] enables pull-to-refresh; [onOpenSaved] adds an "Open Saved" way out of connection errors.
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ArticleFeed(
    feed: PagedArticles,
    bookmarkedUrls: Set<String>,
    onFetchNextPage: () -> Unit,
    onRetry: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onBookmarkTapped: (Article) -> Unit,
    modifier: Modifier = Modifier,
    showLead: Boolean = true,
    timeStyle: TimeStyle = TimeStyle.Clock,
    // Day headers keep clock times unambiguous; only meaningful when the list is in date order
    groupByDay: Boolean = true,
    // Names the first header ("Yesterday's top stories · as of 3:05 PM"); null shows plain day labels
    bulletinName: String? = null,
    listState: LazyListState = rememberLazyListState(),
    onRefresh: (() -> Unit)? = null,
    onOpenSaved: (() -> Unit)? = null,
    emptyContent: @Composable () -> Unit = {
        StatusMessage(
            icon = Icons.Outlined.Newspaper,
            title = stringResource(R.string.feed_empty_title),
            body = stringResource(R.string.feed_empty_body)
        )
    },
) {
    if (feed.articles.isNotEmpty() && !feed.isLoading) {
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
            val days = remember(feed.articles) {
                val now = System.currentTimeMillis()
                feed.articles.map { article -> parsePublishedAt(article.publishedAt)?.let { storyDay(it, now) } }
            }

            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                feed.articles.forEachIndexed { index, article ->
                    val day = days[index]
                    if (groupByDay && day != null && (index == 0 || day != days[index - 1])) {
                        item(key = "day-$index") {
                            val label = dayLabel(day)
                            val text = if (index == 0 && bulletinName != null) {
                                bulletinHeader(day, label, bulletinName, feed.loadedAt)
                            } else {
                                label
                            }
                            ListHeader(text)
                        }
                    }
                    item(key = article.url ?: "no-url-$index") {
                        val onClick = article.url?.let { url -> { onArticleTapped(url) } }
                        val onBookmarkClick = article.url?.let { { onBookmarkTapped(article) } }
                        val isBookmarked = article.url in bookmarkedUrls

                        if (showLead && index == 0) {
                            LeadStory(article, onClick, isBookmarked, onBookmarkClick, Modifier.animateItemPlacement())
                            // A strip of ground separates the lead from the running order
                            Spacer(Modifier.height(8.dp))
                        } else {
                            RunningOrderRow(
                                article = article,
                                onClick = onClick,
                                isBookmarked = isBookmarked,
                                onBookmarkClick = onBookmarkClick,
                                timeStyle = timeStyle,
                                modifier = Modifier.animateItemPlacement()
                            )
                        }
                    }
                }

                if (feed.isLoadingMore) {
                    item(key = "loading-more") {
                        LoadMoreIndicator()
                    }
                } else if (feed.error != null) {
                    item(key = "page-error") {
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
        Box(modifier = modifier.fillMaxSize()) {
            when {
                feed.isLoading -> Loader()
                feed.error != null -> FeedErrorState(feed.error, onRetry, onOpenSaved)
                else -> emptyContent()
            }
        }
    }
}

@Composable
private fun FeedErrorState(error: ErrorType, onRetry: () -> Unit, onOpenSaved: (() -> Unit)?) {
    StatusMessage(
        icon = if (error.isConnectivity) Icons.Outlined.CloudOff else Icons.Outlined.ErrorOutline,
        title = stringResource(error.messageRes()),
        body = stringResource(error.bodyRes()),
        primaryAction = stringResource(R.string.retry) to onRetry,
        // Saved stories live on the device, so they are the way forward when the network is the problem
        secondaryAction = onOpenSaved?.takeIf { error.isConnectivity }?.let { stringResource(R.string.open_saved) to it }
    )
}

/** "Yesterday's top stories · as of 3:05 PM": says plainly how old the bulletin is and when it was fetched. */
@Composable
private fun bulletinHeader(day: StoryDay, dayLabel: String, bulletinName: String, loadedAt: Long?): String {
    val title = when (day) {
        StoryDay.Today -> stringResource(R.string.feed_header_today, bulletinName)
        StoryDay.Yesterday -> stringResource(R.string.feed_header_yesterday, bulletinName)
        is StoryDay.OnDate -> stringResource(R.string.feed_header_date, bulletinName, dayLabel)
    }
    return if (loadedAt != null) stringResource(R.string.feed_header_as_of, title, formatClock(loadedAt)) else title
}
