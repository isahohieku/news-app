package com.ohieku.newsapp.ui.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
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
    private val bookmarkRepository: BookmarkRepository,
) : ViewModel() {

    val uiState: StateFlow<BookmarksUiState> = bookmarkRepository.observeBookmarks()
        .map { bookmarks -> BookmarksUiState(isLoading = false, bookmarks = bookmarks) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, BookmarksUiState())

    /** Removes a saved story, or puts it back when it was just removed (Undo). */
    fun setBookmarked(article: Article, saved: Boolean) {
        if (article.url == null) return
        viewModelScope.launch {
            if (saved) bookmarkRepository.addBookmark(article) else bookmarkRepository.removeBookmark(article.url)
        }
    }

    fun toggleBookmark(article: Article) {
        val url = article.url ?: return
        val isSaved = uiState.value.bookmarks.any { it.url == url }
        viewModelScope.launch {
            if (isSaved) bookmarkRepository.removeBookmark(url) else bookmarkRepository.addBookmark(article)
        }
    }
}
