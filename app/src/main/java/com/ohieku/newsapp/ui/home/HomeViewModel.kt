package com.ohieku.newsapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.usecase.ObserveBookmarkedUrlsUseCase
import com.ohieku.newsapp.domain.features.bookmarks.usecase.SetBookmarkedUseCase
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
import com.ohieku.newsapp.ui.common.StartupReadiness
import com.ohieku.newsapp.ui.common.paging.ArticlePager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    observeBookmarkedUrls: ObserveBookmarkedUrlsUseCase,
    private val setBookmarkedUseCase: SetBookmarkedUseCase,
    startupReadiness: StartupReadiness,
) : ViewModel() {
    private val category = MutableStateFlow(NewsCategory.Top)
    private val pager = ArticlePager(viewModelScope) { page -> getNewsUseCase(category.value, page) }

    val uiState: StateFlow<HomeUiState> = combine(
        category,
        pager.state,
        observeBookmarkedUrls(),
    ) { category, feed, bookmarkedUrls ->
        HomeUiState(category = category, feed = feed, bookmarkedUrls = bookmarkedUrls)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState.initial())

    init {
        pager.fetchNextPage()
        // The splash screen holds until the first page has loaded or failed
        viewModelScope.launch {
            pager.state.first { !it.isLoading }
            startupReadiness.markReady()
        }
    }

    fun fetchNextPage() = pager.fetchNextPage()

    fun retry() = pager.retry()

    fun refresh() = pager.refresh()

    fun refreshErrorShown() = pager.refreshErrorShown()

    fun selectCategory(category: NewsCategory) {
        if (category == this.category.value) return
        this.category.value = category
        pager.start()
    }

    fun setBookmarked(article: Article, saved: Boolean) {
        viewModelScope.launch { setBookmarkedUseCase(article, saved) }
    }
}
