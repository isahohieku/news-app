package com.ohieku.newsapp.ui.bookmarks.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.R
import com.ohieku.newsapp.ui.bookmarks.BookmarksUiState
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.home.compose.HeaderText
import com.ohieku.newsapp.ui.home.compose.Loader
import com.ohieku.newsapp.ui.home.compose.NewsCardComponent

@Composable
internal fun BookmarksScreen(
    state: BookmarksUiState,
    onArticleTapped: (url: String) -> Unit,
    onRemoveTapped: (url: String) -> Unit
) {
    MainScaffold(title = stringResource(R.string.bookmarks_title)) { paddingValues ->
        when {
            state.isLoading -> Loader()

            state.bookmarks.isEmpty() -> Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                HeaderText(text = stringResource(R.string.bookmarks_empty), centerText = true)
            }

            else -> LazyColumn(
                contentPadding = PaddingValues(vertical = 8.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Bookmarks always have a URL; it is the table's key
                items(state.bookmarks, key = { it.url.orEmpty() }) { article ->
                    val url = article.url.orEmpty()
                    NewsCardComponent(
                        article = article,
                        onClick = { onArticleTapped(url) },
                        isBookmarked = true,
                        onBookmarkClick = { onRemoveTapped(url) }
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun BookmarksScreenPreview() {
    BookmarksScreen(
        state = BookmarksUiState(isLoading = false),
        onArticleTapped = { /* no-op */ },
        onRemoveTapped = { /* no-op */ }
    )
}
