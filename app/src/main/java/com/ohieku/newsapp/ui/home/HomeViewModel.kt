package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.data.network.ErrorType
import com.ohieku.newsapp.domain.features.news.model.Article
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

    private fun getNews(page: Int) {
        _uiState.update { currentState ->
            currentState.copy(isLoadingMore = currentState.articles.isNotEmpty())
        }

        loadJob = loadData {
            when (val result = getNewsUseCase(page)) {
                is ResourceState.Success -> {
                    val newArticles = result.data.articles
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalResults = if (newArticles.isEmpty()) currentState.totalResults else result.data.totalResults,
                            isLoading = false,
                            isLoadingMore = false,
                            error = null,
                            articles = currentState.articles.appendDistinct(newArticles),
                            currentPage = page,
                            endReached = newArticles.isEmpty(),
                        )
                    }
                }

                is ResourceState.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isLoadingMore = false,
                            error = (result.error as? ApiException)?.type ?: ErrorType.Unknown
                        )
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

    // NewsAPI can repeat an article across pages as headlines shift; keep the first copy
    private fun List<Article>.appendDistinct(newArticles: List<Article>): List<Article> {
        val seenUrls = mapNotNullTo(HashSet()) { it.url }
        return this + newArticles.filter { article -> article.url == null || seenUrls.add(article.url) }
    }
}
