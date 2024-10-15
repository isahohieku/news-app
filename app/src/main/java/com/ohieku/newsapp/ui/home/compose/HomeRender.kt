package com.ohieku.newsapp.ui.home.compose

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import com.ohieku.newsapp.ui.home.HomeViewModel

@Composable
fun renderHomeScreen() {
    val viewModel: HomeViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    HomeScreen(
        state = state,
        onFetchNextPageTapped = viewModel::fetchNextPage
    )
}