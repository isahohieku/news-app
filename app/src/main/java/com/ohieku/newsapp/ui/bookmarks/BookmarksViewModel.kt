package com.ohieku.newsapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.usecase.ObserveBookmarksUseCase
import com.ohieku.newsapp.domain.features.bookmarks.usecase.SetBookmarkedUseCase
import com.ohieku.newsapp.domain.features.news.model.Article
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    observeBookmarks: ObserveBookmarksUseCase,
    private val setBookmarkedUseCase: SetBookmarkedUseCase,
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> = observeBookmarks()
        .map { bookmarks -> BookmarksUiState(isLoading = false, bookmarks = bookmarks) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BookmarksUiState())

    /** Removes a saved story, or puts it back when it was just removed (Undo). */
    fun setBookmarked(article: Article, saved: Boolean) {
        viewModelScope.launch { setBookmarkedUseCase(article, saved) }
    }
}
