package com.ohieku.newsapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.usecase.SearchNewsUseCase
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
) : ViewModel() {
    private val query = MutableStateFlow("")

    // The query the current results belong to; typing changes [query] first and this after the debounce
    private var activeQuery = ""

    private val pager = ArticlePager(viewModelScope, initialState = PagedArticles.Idle) { page ->
        searchNewsUseCase(activeQuery, page)
    }

    val uiState: StateFlow<SearchUiState> = combine(
        query,
        pager.state,
        bookmarkRepository.observeBookmarkedUrls(),
    ) { query, results, bookmarkedUrls ->
        SearchUiState(query = query, results = results, bookmarkedUrls = bookmarkedUrls)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, SearchUiState())

    init {
        query.debounce(SEARCH_DEBOUNCE_MS)
            .onEach(::applyQuery)
            .launchIn(viewModelScope)
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    /** Searches straight away, e.g. from the keyboard's search key. */
    fun search() = applyQuery(query.value)

    fun fetchNextPage() = pager.fetchNextPage()

    fun retry() = pager.retry()

    fun toggleBookmark(article: Article) {
        val url = article.url ?: return
        val isBookmarked = url in uiState.value.bookmarkedUrls
        viewModelScope.launch {
            if (isBookmarked) bookmarkRepository.removeBookmark(url) else bookmarkRepository.addBookmark(article)
        }
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
