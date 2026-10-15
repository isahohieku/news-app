package com.ohieku.newsapp.ui.search.compose

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.ohieku.newsapp.ui.common.rememberBookmarkFeedback
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.home.compose.ArticleFeed
import com.ohieku.newsapp.ui.home.compose.StatusMessage
import com.ohieku.newsapp.ui.search.SearchUiState

@Composable
internal fun SearchScreen(
    state: SearchUiState,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onBackTapped: () -> Unit,
    onFetchNextPageTapped: () -> Unit,
    onRetryTapped: () -> Unit,
    onArticleTapped: (url: String) -> Unit,
    onSetBookmarked: (article: Article, saved: Boolean) -> Unit,
    onOpenSaved: () -> Unit
) {
    val colors = LocalNewsColors.current
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current
    val snackbarHostState = remember { SnackbarHostState() }
    val onBookmarkTapped = rememberBookmarkFeedback(
        snackbarHostState = snackbarHostState,
        bookmarkedUrls = state.bookmarkedUrls,
        onSetBookmarked = onSetBookmarked,
        onViewSaved = onOpenSaved
    )

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    MainScaffold(
        snackbarHostState = snackbarHostState,
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
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    onSearch()
                }),
                trailingIcon = {
                    if (state.query.isNotEmpty()) {
                        IconButton(onClick = {
                            onQueryChange("")
                            // Stay in the field so the next query can be typed straight away
                            focusRequester.requestFocus()
                            keyboardController?.show()
                        }) {
                            Icon(Icons.Filled.Close, contentDescription = stringResource(R.string.clear_search))
                        }
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = colors.onChrome,
                    unfocusedTextColor = colors.onChrome,
                    cursorColor = colors.onChrome,
                    focusedPlaceholderColor = colors.onChromeMuted,
                    unfocusedPlaceholderColor = colors.onChromeMuted,
                    focusedTrailingIconColor = colors.onChrome,
                    unfocusedTrailingIconColor = colors.onChrome,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester)
            )
        }
    ) { paddingValues ->
        if (state.results.isIdle) {
            StatusMessage(
                icon = Icons.Outlined.Search,
                title = stringResource(R.string.search_hint_title),
                body = stringResource(R.string.search_hint_body),
                alignTop = true,
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
                showLead = false,
                onOpenSaved = onOpenSaved,
                modifier = Modifier.padding(paddingValues),
                emptyContent = {
                    StatusMessage(
                        icon = Icons.Outlined.SearchOff,
                        title = stringResource(R.string.search_no_results, state.query.trim()),
                        body = stringResource(R.string.search_no_results_body),
                        alignTop = true
                    )
                }
            )
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    NewsAppTheme {
        SearchScreen(
            state = SearchUiState(),
            onQueryChange = { /* no-op */ },
            onSearch = { /* no-op */ },
            onBackTapped = { /* no-op */ },
            onFetchNextPageTapped = { /* no-op */ },
            onRetryTapped = { /* no-op */ },
            onArticleTapped = { /* no-op */ },
            onSetBookmarked = { _, _ -> /* no-op */ },
            onOpenSaved = { /* no-op */ }
        )
    }
}
