package com.ohieku.newsapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.data.AppConstants
import com.ohieku.newsapp.data.entity.NewsResponse
import com.ohieku.newsapp.ui.repository.NewsRepository
import com.ohieku.utilities.ResourceState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NewsViewModel @Inject constructor(
    private val newsRepository: NewsRepository
): ViewModel() {

    private val _news : MutableStateFlow<ResourceState<NewsResponse>> =
        MutableStateFlow(ResourceState.Loading())
    val news: StateFlow<ResourceState<NewsResponse>> = _news

    init {
        getNews(AppConstants.APP_DEFAULT_COUNTRY)
    }

    private fun getNews(country: String) {
        viewModelScope.launch(Dispatchers.IO) {
            newsRepository.getNewsHeadline(country)
                .collectLatest { newsResponse ->
                    _news.value = newsResponse
                }
        }
    }
}