package com.ohieku.newsapp.ui.search.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.ui.home.compose.ArticleFeed
import com.ohieku.newsapp.ui.home.compose.HeaderText
import com.ohieku.newsapp.ui.search.SearchUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBackTapped: () -> Unit,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onBookmarkTapped: (Article) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBackTapped) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                },
                title = {
                    TextField(
                        value = state.query,
                        onValueChange = onQueryChange,
                        placeholder = { Text(stringResource(R.string.search_placeholder)) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions = KeyboardActions(onSearch = {
                            keyboardController?.hide()
                            onSearch()
                        }),
                        trailingIcon = {
                            if (state.query.isNotEmpty()) {
                                IconButton(onClick = { onQueryChange("") }) {
                                    Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.clear_search))
                                }
                            }
                        },
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .focusRequester(focusRequester)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        if (state.results.isIdle) {
            SearchMessage(
                text = stringResource(R.string.search_hint),
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            ArticleFeed(
                feed = state.results,
                bookmarkedUrls = state.bookmarkedUrls,
                onFetchNextPage = onFetchNextPageTapped,
                onRetry = onRetryTapped,
                onArticleTapped = onArticleTapped,
                onBookmarkTapped = onBookmarkTapped,
                modifier = Modifier.padding(paddingValues),
                emptyContent = {
                    SearchMessage(text = stringResource(R.string.search_no_results, state.query.trim()))
                }
            )
        }
    }
}

@Composable
private fun SearchMessage(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center
    ) {
        HeaderText(text = text, centerText = true)
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    SearchScreen(
        state = SearchUiState(),
        onQueryChange = { /* no-op */ },
        onSearch = { /* no-op */ },
        onBackTapped = { /* no-op */ },
        onFetchNextPageTapped = { /* no-op */ },
        onRetryTapped = { /* no-op */ },
        onArticleTapped = { /* no-op */ },
        onBookmarkTapped = { /* no-op */ }
    )
}
