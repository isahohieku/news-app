package com.ohieku.newsapp.ui.home

import androidx.lifecycle.DefaultLifecycleObserver
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
): BaseViewModel(), DefaultLifecycleObserver {
    private val _uiState: MutableStateFlow<HomeUiState> = MutableStateFlow(HomeUiState.initial())
    val uiState: StateFlow<HomeUiState>
        get() = _uiState

    init {
        getNews()
    }

    private fun getNews() {
        val page = _uiState.value.currentPage
        loadData {
            when (val result = getNewsUseCase(page)) {
                is ResourceState.Success -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            totalPages = result.data.totalResults,
                            isLoading = false,
                            articles = currentState.articles + result.data.articles,
                        )
                    }
                }

                is ResourceState.Error -> {
                    _uiState.update { currentState ->
                        currentState.copy(
                            isLoading = false,
                            isError = true
                        )
                    }
                }
            }
        }
    }

    fun fetchNextPage() {
        _uiState.update { currentState ->
            currentState.copy(
                currentPage = currentState.currentPage + 1
            )
        }
        getNews()
    }
}