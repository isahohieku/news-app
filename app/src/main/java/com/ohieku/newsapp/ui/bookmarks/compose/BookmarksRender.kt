package com.ohieku.newsapp.ui.bookmarks.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ohieku.newsapp.extensions.utils.openInBrowser
import com.ohieku.newsapp.ui.bookmarks.BookmarksViewModel

@Composable
fun renderBookmarksScreen() {
    val viewModel: BookmarksViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    BookmarksScreen(
        state = state,
        onArticleTapped = { url -> context.openInBrowser(url) },
        onSetBookmarked = viewModel::setBookmarked
    )
}
