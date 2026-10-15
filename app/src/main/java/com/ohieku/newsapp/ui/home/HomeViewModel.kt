package com.ohieku.newsapp.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.usecase.GetNewsUseCase
import com.ohieku.newsapp.ui.common.paging.ArticlePager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {
    private val category = MutableStateFlow(NewsCategory.Top)
    private val pager = ArticlePager(viewModelScope) { page -> getNewsUseCase(category.value, page) }

    val uiState: StateFlow<HomeUiState> = combine(
        category,
        pager.state,
        bookmarkRepository.observeBookmarkedUrls(),
    ) { category, feed, bookmarkedUrls ->
        HomeUiState(category = category, feed = feed, bookmarkedUrls = bookmarkedUrls)
    }.stateIn(viewModelScope, SharingStarted.Eagerly, HomeUiState.initial())

    init {
        pager.fetchNextPage()
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
        if (article.url == null) return
        viewModelScope.launch {
            if (saved) bookmarkRepository.addBookmark(article) else bookmarkRepository.removeBookmark(article.url)
        }
    }

    fun toggleBookmark(article: Article) {
        val url = article.url ?: return
        val isBookmarked = url in uiState.value.bookmarkedUrls
        viewModelScope.launch {
            if (isBookmarked) bookmarkRepository.removeBookmark(url) else bookmarkRepository.addBookmark(article)
        }
    }
}
