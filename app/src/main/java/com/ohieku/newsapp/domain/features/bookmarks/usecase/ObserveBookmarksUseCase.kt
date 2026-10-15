package com.ohieku.newsapp.domain.features.bookmarks.usecase

import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveBookmarksUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /** Saved articles, most recently saved first. */
    operator fun invoke(): Flow<List<Article>> = bookmarkRepository.observeBookmarks()
}
