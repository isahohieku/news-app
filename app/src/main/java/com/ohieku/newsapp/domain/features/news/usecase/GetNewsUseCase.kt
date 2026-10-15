package com.ohieku.newsapp.domain.features.news.usecase

import com.ohieku.newsapp.domain.common.ResourceState
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(
        category: NewsCategory,
        page: Int
    ): ResourceState<NewsModel> {
        return newsRepository.getNewsHeadline(category, page)
    }
}