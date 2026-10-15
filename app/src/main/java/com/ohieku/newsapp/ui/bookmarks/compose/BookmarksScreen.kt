package com.ohieku.newsapp.ui.bookmarks.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.ui.bookmarks.BookmarksUiState
import com.ohieku.newsapp.ui.common.rememberArticleOpener
import com.ohieku.newsapp.ui.common.rememberBookmarkFeedback
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.ScreenTitle
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.home.compose.Loader
import com.ohieku.newsapp.ui.home.compose.RunningOrderRow
import com.ohieku.newsapp.ui.home.compose.StatusMessage
import com.ohieku.newsapp.ui.home.compose.TimeStyle

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun BookmarksScreen(
    state: BookmarksUiState,
    onArticleTapped: (url: String) -> Unit,
    onSetBookmarked: (article: Article, saved: Boolean) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // Removing here offers Undo; there is nowhere else to "View"
    val onBookmarkTapped = rememberBookmarkFeedback(
        snackbarHostState = snackbarHostState,
        bookmarkedUrls = remember(state.bookmarks) { state.bookmarks.mapNotNull { it.url }.toSet() },
        onSetBookmarked = onSetBookmarked,
        onViewSaved = null
    )
    val openArticle = rememberArticleOpener(snackbarHostState, onArticleTapped)

    MainScaffold(
        title = { ScreenTitle(stringResource(R.string.bookmarks_title)) },
        snackbarHostState = snackbarHostState
    ) { paddingValues ->
        when {
            state.isLoading -> Loader(Modifier.padding(paddingValues))

            state.bookmarks.isEmpty() -> StatusMessage(
                icon = Icons.Outlined.BookmarkBorder,
                title = stringResource(R.string.bookmarks_empty_title),
                body = stringResource(R.string.bookmarks_empty_body),
                modifier = Modifier.padding(paddingValues)
            )

            else -> LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Bookmarks always have a URL; it is the table's key
                items(state.bookmarks, key = { it.url.orEmpty() }) { article ->
                    val url = article.url.orEmpty()
                    RunningOrderRow(
                        article = article,
                        onClick = { openArticle(url) },
                        isBookmarked = true,
                        onBookmarkClick = { onBookmarkTapped(article) },
                        // Saved is ordered by when stories were saved, so each row carries its own date
                        timeStyle = TimeStyle.DayOrClock,
                        showDescription = true,
                        modifier = Modifier.animateItemPlacement()
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BookmarksScreenPreview() {
    NewsAppTheme {
        BookmarksScreen(
            state = BookmarksUiState(isLoading = false),
            onArticleTapped = { /* no-op */ },
            onSetBookmarked = { _, _ -> /* no-op */ }
        )
    }
}
