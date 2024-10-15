package com.ohieku.newsapp.extensions.views

import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.ViewCompositionStrategy
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme

fun Fragment.compose(
    content: @Composable () -> Unit
) = ComposeView(requireContext()).apply {
    setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
    setContent {
        NewsAppTheme {
            content()
        }
    }
}