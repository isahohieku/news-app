package com.ohieku.newsapp.extensions.utils

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme

fun ComposeView.bind(content: @Composable () -> Unit): ComposeView {
    return apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            NewsAppTheme {
                content()
            }
        }
    }
}