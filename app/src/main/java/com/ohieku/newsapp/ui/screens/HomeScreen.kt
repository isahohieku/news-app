package com.ohieku.newsapp.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PageSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohieku.newsapp.ui.components.EmptyState
import com.ohieku.newsapp.ui.components.Loader
import com.ohieku.newsapp.ui.components.NewsRowComponent
import com.ohieku.newsapp.ui.viewmodel.NewsViewModel
import com.ohieku.utilities.ResourceState

const val TAG = "HOME SCREEN"

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    newsViewModel: NewsViewModel = hiltViewModel()
) {

    val newsResponse by newsViewModel.news.collectAsState()

    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f
    ) {
        100
    }

    VerticalPager(state = pagerState,
        modifier = Modifier.fillMaxSize(),
        pageSize = PageSize.Fill,
        pageSpacing = 8.dp
    ) {page:Int ->
        when(newsResponse) {
            is ResourceState.Loading -> {
                Loader()
                Log.d(TAG, "Inside_Loading")
            }

            is ResourceState.Success -> {
                val response = (newsResponse as ResourceState.Success).data
                Log.d(TAG, "Inside_Success ${response.status} = ${response.totalResults}")
                if (response.articles.isNotEmpty()) {
                    NewsRowComponent(page, response.articles[page])
                } else {
                    EmptyState()
                }
            }

            is ResourceState.Error -> {
                val error = newsResponse as ResourceState.Error
                Log.d(TAG, "Inside_Error $error")
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}
