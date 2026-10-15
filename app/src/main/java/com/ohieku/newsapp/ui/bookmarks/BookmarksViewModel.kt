package com.ohieku.newsapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> = bookmarkRepository.observeBookmarks()
        .map { bookmarks -> BookmarksUiState(isLoading = false, bookmarks = bookmarks) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BookmarksUiState())

    fun removeBookmark(url: String) {
        viewModelScope.launch { bookmarkRepository.removeBookmark(url) }
    }
}
