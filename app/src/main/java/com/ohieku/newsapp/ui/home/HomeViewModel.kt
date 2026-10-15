package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
import com.ohieku.newsapp.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
): BaseViewModel() {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.initial())
    val uiState: StateFlow<HomeUiState>
        get() = _uiState

    private var loadJob: Job? = null

    init {
        fetchNextPage()
    }

    /** Loads [page] of the current category; [replace] swaps the list instead of appending (refresh). */
    private fun getNews(page: Int, replace: Boolean = false) {
        if (!replace) {
            _uiState.update { currentState ->
                currentState.copy(isLoadingMore = currentState.articles.isNotEmpty())
            }
        }
        val category = _uiState.value.category

        loadJob = loadData {
            when (val result = getNewsUseCase(category, page)) {
                is ResourceState.Success -> {
                    val newArticles = result.data.articles
                    _uiState.update { currentState ->
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
                    _uiState.update { currentState ->
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

    fun fetchNextPage() {
        val state = _uiState.value
        if (loadJob?.isActive == true || state.error != null || !state.hasMorePages) return
        getNews(state.currentPage + 1)
    }

    fun retry() {
        if (loadJob?.isActive == true) return
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = currentState.articles.isEmpty(),
                error = null
            )
        }
        fetchNextPage()
    }

    fun selectCategory(category: NewsCategory) {
        if (category == _uiState.value.category) return
        loadJob?.cancel()
        _uiState.value = HomeUiState(category = category)
        fetchNextPage()
    }

    fun refresh() {
        loadJob?.cancel()
        _uiState.update { currentState ->
            currentState.copy(isRefreshing = true, isLoadingMore = false, refreshError = null)
        }
        getNews(page = 1, replace = true)
    }

    fun refreshErrorShown() {
        _uiState.update { currentState -> currentState.copy(refreshError = null) }
    }

    // NewsAPI can repeat an article across pages as headlines shift; keep the first copy
    private fun List<Article>.appendDistinct(newArticles: List<Article>): List<Article> {
        val seenUrls = mapNotNullTo(HashSet()) { it.url }
        return this + newArticles.filter { article -> article.url == null || seenUrls.add(article.url) }
    }
}
