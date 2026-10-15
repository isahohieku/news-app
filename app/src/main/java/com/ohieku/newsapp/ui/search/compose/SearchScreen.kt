package com.ohieku.newsapp.ui.search.compose

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ohieku.newsapp.R
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.SearchSort
import com.ohieku.newsapp.ui.common.rememberArticleOpener
import com.ohieku.newsapp.ui.common.rememberBookmarkFeedback
import com.ohieku.newsapp.ui.compose.components.MainScaffold
import com.ohieku.newsapp.ui.compose.components.labelRes
import com.ohieku.newsapp.ui.compose.theme.LocalNewsColors
import com.ohieku.newsapp.ui.compose.theme.NewsAppTheme
import com.ohieku.newsapp.ui.home.compose.ArticleFeed
import com.ohieku.newsapp.ui.home.compose.ListHeader
import com.ohieku.newsapp.ui.home.compose.StatusMessage
import com.ohieku.newsapp.ui.home.compose.TimeStyle
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
    onOpenSaved: () -> Unit,
    onSortChange: (SearchSort) -> Unit,
    onRecentSearchTapped: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onBrowseCategory: (NewsCategory) -> Unit
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
    val openArticle = rememberArticleOpener(snackbarHostState, onArticleTapped)

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
            SearchStart(
                recentSearches = state.recentSearches,
                onRecentSearchTapped = onRecentSearchTapped,
                onClearRecentSearches = onClearRecentSearches,
                onBrowseCategory = onBrowseCategory,
                modifier = Modifier.padding(paddingValues)
            )
        } else {
            Column(modifier = Modifier.padding(paddingValues)) {
                SortRow(sort = state.sort, onSortChange = onSortChange)
                ArticleFeed(
                    feed = state.results,
                    bookmarkedUrls = state.bookmarkedUrls,
                    onFetchNextPage = onFetchNextPageTapped,
                    onRetry = onRetryTapped,
                    onArticleTapped = openArticle,
                    onBookmarkTapped = onBookmarkTapped,
                    showLead = false,
                    // Latest results are in date order, so they group by day like the feed; relevant ones show dates
                    groupByDay = state.sort == SearchSort.Latest,
                    timeStyle = if (state.sort == SearchSort.Latest) TimeStyle.Clock else TimeStyle.DayOrClock,
                    onOpenSaved = onOpenSaved,
                    modifier = Modifier.weight(1f),
                    emptyContent = {
                        StatusMessage(
                            icon = Icons.Outlined.SearchOff,
                            title = stringResource(R.string.search_no_results, state.query.trim()),
                            body = stringResource(R.string.search_no_results_body),
                            alignTop = true,
                            primaryAction = stringResource(R.string.browse_top_stories) to {
                                onBrowseCategory(NewsCategory.Top)
                            }
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortRow(sort: SearchSort, onSortChange: (SearchSort) -> Unit) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        listOf(
            SearchSort.Relevance to R.string.search_sort_relevant,
            SearchSort.Latest to R.string.search_sort_latest,
        ).forEach { (option, labelRes) ->
            FilterChip(
                selected = sort == option,
                onClick = { onSortChange(option) },
                label = { Text(stringResource(labelRes)) },
                shape = RectangleShape,
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = MaterialTheme.colorScheme.onBackground,
                    selectedLabelColor = MaterialTheme.colorScheme.background,
                    labelColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

/** Before a query: recent searches to repeat, and the categories to browse instead. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SearchStart(
    recentSearches: List<String>,
    onRecentSearchTapped: (String) -> Unit,
    onClearRecentSearches: () -> Unit,
    onBrowseCategory: (NewsCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        if (recentSearches.isNotEmpty()) {
            ListHeader(
                text = stringResource(R.string.search_recent),
                action = {
                    // Ink, not red: this sits on the grey ground, where red text falls below AA
                    TextButton(
                        onClick = onClearRecentSearches,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text(stringResource(R.string.search_clear_recent))
                    }
                }
            )
            recentSearches.forEach { recent ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 48.dp)
                        .clickable { onRecentSearchTapped(recent) }
                        .padding(horizontal = 16.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.History,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = recent,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }

        ListHeader(text = stringResource(R.string.search_browse))
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            NewsCategory.entries.forEach { category ->
                SuggestionChip(
                    onClick = { onBrowseCategory(category) },
                    label = { Text(stringResource(category.labelRes)) },
                    shape = RectangleShape
                )
            }
        }
    }
}

@Preview
@Composable
fun SearchScreenPreview() {
    NewsAppTheme {
        SearchScreen(
            state = SearchUiState(recentSearches = listOf("climate", "NLCS")),
            onQueryChange = { /* no-op */ },
            onSearch = { /* no-op */ },
            onBackTapped = { /* no-op */ },
            onFetchNextPageTapped = { /* no-op */ },
            onRetryTapped = { /* no-op */ },
            onArticleTapped = { /* no-op */ },
            onSetBookmarked = { _, _ -> /* no-op */ },
            onOpenSaved = { /* no-op */ },
            onSortChange = { /* no-op */ },
            onRecentSearchTapped = { /* no-op */ },
            onClearRecentSearches = { /* no-op */ },
            onBrowseCategory = { /* no-op */ }
        )
    }
}
