package com.ohieku.newsapp.ui.compose.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.TextButton
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.R
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors

/** The broadcast wordmark: the name in caps. */
@Composable
fun Wordmark() {
    val name = stringResource(R.string.app_title)
    Text(
        text = name.uppercase(),
        style = MaterialTheme.typography.titleLarge,
        modifier = Modifier.clearAndSetSemantics {
            contentDescription = name
            heading()
        }
    )
}

/**
 * Snackbar in the broadcast system: a square, flat bar (no Material rounding or shadow) with a coral action
 * that clears AA in both themes.
 */
@Composable
fun NewsSnackbar(data: SnackbarData) {
    val colors = LocalNewsColors.current
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth()
            .heightIn(min = 48.dp)
            .background(colors.snackbar)
            .border(1.dp, colors.snackbarEdge)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(start = 16.dp, end = 4.dp)
    ) {
        Text(
            text = data.visuals.message,
            style = MaterialTheme.typography.bodyMedium,
            color = colors.onSnackbar,
            modifier = Modifier
                .weight(1f)
                .padding(vertical = 12.dp)
        )
        data.visuals.actionLabel?.let { label ->
            TextButton(
                onClick = { data.performAction() },
                colors = ButtonDefaults.textButtonColors(contentColor = colors.snackbarAction)
            ) {
                Text(label)
            }
        }
    }
}

/** A screen title in the same voice as the wordmark. */
@Composable
fun ScreenTitle(text: String) {
    Text(text = text.uppercase(), style = MaterialTheme.typography.titleLarge)
}

/** Screen frame: the dark broadcast bar (title, actions, optional pinned [tabs]) over the grey ground. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    title: @Composable () -> Unit = { Wordmark() },
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    navigationIcon: @Composable () -> Unit = {},
    actions: @Composable RowScope.() -> Unit = {},
    tabs: (@Composable () -> Unit)? = null,
    content: @Composable (PaddingValues) -> Unit
) {
    val colors = LocalNewsColors.current
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        snackbarHost = { SnackbarHost(snackbarHostState) { data -> NewsSnackbar(data) } },
        topBar = {
            Column(modifier = Modifier.background(colors.chrome)) {
                TopAppBar(
                    title = title,
                    navigationIcon = navigationIcon,
                    actions = actions,
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = colors.chrome,
                        titleContentColor = colors.onChrome,
                        navigationIconContentColor = colors.onChrome,
                        actionIconContentColor = colors.onChrome,
                    )
                )
                tabs?.invoke()
            }
        }
    ) { paddingValues ->
        content(paddingValues)
    }
}
