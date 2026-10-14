package com.ohieku.newsapp.ui.home

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
import com.ohieku.newsapp.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    init {
        fetchNextPage()
    }

    private fun getNews(page: Int) {
        loadData {
            when (val result = getNewsUseCase(page)) {
                is ResourceState.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalPages = result.data.totalResults,
                            isLoading = false,
                            errorMessage = null,
                            articles = currentState.articles + result.data.articles,
                            currentPage = page,
                        )
                    }
                }

                is ResourceState.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            errorMessage = result.error.message ?: DEFAULT_ERROR_MESSAGE
                        )
                    }
                }
            }
        }
    }

    fun fetchNextPage() {
        getNews(_uiState.value.currentPage + 1)
    }

    fun retry() {
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
