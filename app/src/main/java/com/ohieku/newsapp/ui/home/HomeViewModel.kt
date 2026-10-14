package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
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
                            errorMessage = null,
                            articles = currentState.articles + newArticles,
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
                            errorMessage = result.error.message ?: DEFAULT_ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    fun fetchNextPage() {
        val state = _uiState.value
        if (loadJob?.isActive == true || state.errorMessage != null || !state.hasMorePages) return
        getNews(state.currentPage + 1)
    }

    fun retry() {
        if (loadJob?.isActive == true) return
        _uiState.update { currentState ->
            currentState.copy(
                isLoading = currentState.articles.isEmpty(),
                errorMessage = null
            )
        }
        fetchNextPage()
    }

    companion object {
        private const val DEFAULT_ERROR_MESSAGE = "Something went wrong, please try again later"
    }
}
