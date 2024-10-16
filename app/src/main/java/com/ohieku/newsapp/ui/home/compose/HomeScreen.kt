package com.ohieku.newsapp.ui.home.compose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.ui.home.HomeUiState

@Composable
internal fun HomeScreen(
    state: HomeUiState,
    onFetchNextPageTapped: () -> Unit
) {
    if (state.isLoading) {
        Loader()
    } else if (!state.isLoading && state.articles.isNotEmpty()) {
        val pagerState = rememberPagerState(
            initialPage = 0,
            initialPageOffsetFraction = 0f
        ) {
            state.totalPages
        }
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            pageSize = PageSize.Fill,
            pageSpacing = 8.dp
        ) { page: Int ->
            NewsRowComponent(page, state.articles[page], state.totalPages)
            if ((page == state.articles.size - 5) && (state.articles.size - 1 < state.totalPages)) {
                LaunchedEffect(page) {
                    // Trigger fetching the next page
                    onFetchNextPageTapped()
                }
            }
        }
    } else {
        EmptyState()
    }

    if (!state.isLoading && state.isError) {
//            val error = newsResponse as ResourceState.Error
//            Log.d(TAG, "Inside_Error $error")
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen(
        state = HomeUiState.initial(),
        onFetchNextPageTapped = { /* no-op */ }
    )
}
