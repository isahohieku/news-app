package com.ohieku.newsapp.domain.features.bookmarks.usecase

import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.model.Article
import javax.inject.Inject

class SetBookmarkedUseCase @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) {
    /** Saves or removes [article]; articles without a URL can't be bookmarked and are ignored. */
    suspend operator fun invoke(article: Article, saved: Boolean) {
        val url = article.url ?: return
        if (saved) bookmarkRepository.addBookmark(article) else bookmarkRepository.removeBookmark(url)
    }
}
