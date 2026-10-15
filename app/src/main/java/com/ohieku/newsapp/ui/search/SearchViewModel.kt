package com.ohieku.newsapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.SearchSort
import com.ohieku.newsapp.domain.features.news.usecase.SearchNewsUseCase
import com.ohieku.newsapp.domain.features.search.repository.RecentSearchRepository
import com.ohieku.newsapp.ui.common.paging.ArticlePager
import com.ohieku.newsapp.ui.common.paging.PagedArticles
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchNewsUseCase: SearchNewsUseCase,
    private val bookmarkRepository: BookmarkRepository,
    private val recentSearchRepository: RecentSearchRepository,
) : ViewModel() {
    private val query = MutableStateFlow("")
    private val sort = MutableStateFlow(SearchSort.Relevance)

    // The query the current results belong to; typing changes [query] first and this after the debounce
    private var activeQuery = ""

    private val pager = ArticlePager(viewModelScope, initialState = PagedArticles.Idle) { page ->
        searchNewsUseCase(activeQuery, sort.value, page)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        pager.state,
        bookmarkRepository.observeBookmarkedUrls(),
        sort,
        recentSearchRepository.observeRecentSearches(),
    ) { query, results, bookmarkedUrls, sort, recentSearches ->
        SearchUiState(
            query = query,
            results = results,
            bookmarkedUrls = bookmarkedUrls,
            sort = sort,
            recentSearches = recentSearches,
        )
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SearchUiState())

    init {
        query.debounce(SEARCH_DEBOUNCE_MS)
            .onEach(::applyQuery)
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    /** Searches straight away, e.g. from the keyboard's search key, and remembers the query. */
    fun search() {
        applyQuery(query.value)
        rememberActiveQuery()
    }

    /** Runs a recent search again. */
    fun selectRecentSearch(value: String) {
        query.value = value
        applyQuery(value)
        rememberActiveQuery()
    }

    fun clearRecentSearches() {
        viewModelScope.launch { recentSearchRepository.clearRecentSearches() }
    }

    fun setSort(value: SearchSort) {
        if (value == sort.value) return
        sort.value = value
        if (activeQuery.length >= MIN_QUERY_LENGTH) pager.start()
    }

    /** Opening a result is the clearest sign the query was worth keeping. */
    fun onResultOpened() = rememberActiveQuery()

    fun fetchNextPage() = pager.fetchNextPage()

    fun retry() = pager.retry()

    fun setBookmarked(article: Article, saved: Boolean) {
        if (article.url == null) return
        viewModelScope.launch {
            if (saved) bookmarkRepository.addBookmark(article) else bookmarkRepository.removeBookmark(article.url)
        }
    }

    fun toggleBookmark(article: Article) {
        setBookmarked(article, saved = article.url !in uiState.value.bookmarkedUrls)
    }

    private fun rememberActiveQuery() {
        val toRemember = activeQuery
        if (toRemember.length < MIN_QUERY_LENGTH) return
        viewModelScope.launch { recentSearchRepository.addRecentSearch(toRemember) }
    }

    private fun applyQuery(value: String) {
        val trimmed = value.trim()
        if (trimmed == activeQuery) return
        activeQuery = trimmed
        if (trimmed.length < MIN_QUERY_LENGTH) pager.clear() else pager.start()
    }

    companion object {
        const val SEARCH_DEBOUNCE_MS = 400L
        const val MIN_QUERY_LENGTH = 2
    }
}
