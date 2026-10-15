package com.ohieku.newsapp.ui.common.paging

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pages through NewsAPI results one request at a time. [loadPage] is read on every request,
 * so callers change what is loaded (category, query) and then call [start].
 */
class ArticlePager(
    private val scope: CoroutineScope,
    initialState: PagedArticles = PagedArticles(),
    private val loadPage: suspend (page: Int) -> ResourceState<NewsModel>,
) {
    private val _state = MutableStateFlow(initialState)
    val state: StateFlow<PagedArticles> = _state.asStateFlow()

    private var loadJob: Job? = null

    /** Drops whatever is loaded or loading and fetches the first page. */
    fun start() {
        loadJob?.cancel()
        _state.value = PagedArticles()
        fetchNextPage()
    }

    /** Drops everything and waits for the next [start]. */
    fun clear() {
        loadJob?.cancel()
        _state.value = PagedArticles.Idle
    }

    fun fetchNextPage() {
        val state = _state.value
        if (loadJob?.isActive == true || state.error != null || !state.hasMorePages) return
        load(state.currentPage + 1)
    }

    fun retry() {
        if (loadJob?.isActive == true) return
        _state.update { currentState ->
            currentState.copy(
                isLoading = currentState.articles.isEmpty(),
                error = null
            )
        }
        fetchNextPage()
    }

    fun refresh() {
        loadJob?.cancel()
        _state.update { currentState ->
            currentState.copy(isRefreshing = true, isLoadingMore = false, refreshError = null)
        }
        load(page = 1, replace = true)
    }

    fun refreshErrorShown() {
        _state.update { currentState -> currentState.copy(refreshError = null) }
    }

    /** Loads [page]; [replace] swaps the list instead of appending (refresh). */
    private fun load(page: Int, replace: Boolean = false) {
        if (!replace) {
            _state.update { currentState ->
                currentState.copy(isLoadingMore = currentState.articles.isNotEmpty())
            }
        }

        loadJob = scope.launch {
            when (val result = loadPage(page)) {
                is ResourceState.Success -> {
                    val newArticles = result.data.articles
                    _state.update { currentState ->
                        val existing = if (replace) emptyList() else currentState.articles
                        currentState.copy(
                            totalResults = if (newArticles.isEmpty()) currentState.totalResults else result.data.totalResults,
                            isLoading = false,
                            isLoadingMore = false,
                            isRefreshing = false,
                            error = null,
                            articles = existing.appendDistinct(newArticles),
                            currentPage = page,
                            endReached = newArticles.isEmpty(),
                        )
                    }
                }

                is ResourceState.Error -> {
                    val type = (result.error as? ApiException)?.type ?: ErrorType.Unknown
                    _state.update { currentState ->
                        if (replace) {
                            currentState.copy(isRefreshing = false, refreshError = type)
                        } else {
                            currentState.copy(
                                isLoading = false,
                                isLoadingMore = false,
                                error = type
                            )
                        }
                    }
                }
            }
        }
    }

    // NewsAPI can repeat an article across pages as headlines shift; keep the first copy
    private fun List<Article>.appendDistinct(newArticles: List<Article>): List<Article> {
        val seenUrls = mapNotNullTo(HashSet()) { it.url }
        return this + newArticles.filter { article -> article.url == null || seenUrls.add(article.url) }
    }
}
