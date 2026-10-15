package com.ohieku.newsapp.ui.common

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ohieku.newsapp.R
import com.ohieku.utilities.CoreUtility
import kotlinx.coroutines.launch

/**
 * Opens stories only when there is a connection. Offline, a snackbar says so instead of handing the reader to the
 * browser's error page, since saved stories are links to the publisher, not offline copies.
 */
@Composable
fun rememberArticleOpener(
    snackbarHostState: SnackbarHostState,
    onOpen: (url: String) -> Unit,
): (url: String) -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val currentOnOpen by rememberUpdatedState(onOpen)
    val offlineMessage = stringResource(R.string.open_offline)
    val connectivity = remember { CoreUtility() }

    return remember(context) {
        { url ->
            if (connectivity.isInternetConnected(context)) {
                currentOnOpen(url)
            } else {
                scope.launch {
                    snackbarHostState.currentSnackbarData?.dismiss()
                    snackbarHostState.showSnackbar(offlineMessage)
                }
            }
        }
    }
}
