package com.ohieku.newsapp.ui.common

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.res.stringResource
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import kotlinx.coroutines.launch

/**
 * Wraps a bookmark toggle with a snackbar: "Saved · View" after saving, "Removed from Saved · Undo" after removing,
 * so a mis-tap never silently loses a story.
 */
@Composable
fun rememberBookmarkFeedback(
    snackbarHostState: SnackbarHostState,
    bookmarkedUrls: Set<String>,
    onSetBookmarked: (article: Article, saved: Boolean) -> Unit,
    onViewSaved: (() -> Unit)?,
): (Article) -> Unit {
    val scope = rememberCoroutineScope()
    val currentUrls by rememberUpdatedState(bookmarkedUrls)
    val savedMessage = stringResource(R.string.bookmark_saved)
    val removedMessage = stringResource(R.string.bookmark_removed)
    val viewLabel = stringResource(R.string.bookmark_view)
    val undoLabel = stringResource(R.string.undo)

    return { article ->
        val wasSaved = article.url in currentUrls
        onSetBookmarked(article, !wasSaved)
        scope.launch {
            snackbarHostState.currentSnackbarData?.dismiss()
            val result = snackbarHostState.showSnackbar(
                message = if (wasSaved) removedMessage else savedMessage,
                actionLabel = if (wasSaved) undoLabel else onViewSaved?.let { viewLabel },
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                // Undo restores explicitly, so a list that hasn't caught up yet can't turn it into a second removal
                if (wasSaved) onSetBookmarked(article, true) else onViewSaved?.invoke()
            }
        }
    }
}
