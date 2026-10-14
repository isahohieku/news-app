package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.ui.home.HomeUiState
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.OverviewSection

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit
) {
    MainScaffold { paddingValues ->
        when {
            state.isLoading -> Loader()

            state.articles.isNotEmpty() -> {
                val listState = rememberLazyListState()

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                ) {
                    item {
                        OverviewSection()
                    }

                    itemsIndexed(state.articles) { index, article ->
                        NewsCardComponent(index, article, state.totalPages)

                        if ((index == state.articles.size - 5) && (state.articles.size - 1 < state.totalPages)) {
                            LaunchedEffect(index) {
                                onFetchNextPageTapped()
                            }
                        }
                    }

                    state.errorMessage?.let { message ->
                        item {
                            PageErrorFooter(message, onRetryTapped)
                        }
                    }
                }
            }

            state.errorMessage != null -> ErrorState(state.errorMessage, onRetryTapped)

            else -> EmptyState()
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        state = HomeUiState.initial(),
        onFetchNextPageTapped = { /* no-op */ },
        onRetryTapped = { /* no-op */ }
    )
}
