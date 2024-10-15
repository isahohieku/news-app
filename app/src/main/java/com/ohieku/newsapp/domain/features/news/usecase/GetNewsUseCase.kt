package com.ohieku.newsapp.domain.features.news.usecase

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.features.news.repository.NewsRepositoryImplementation
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import javax.inject.Inject

class GetNewsUseCase @Inject constructor(
    private val newsRepository: NewsRepositoryImplementation
) {
    suspend operator fun invoke(
        page: Int
    ): ResourceState<NewsModel> {
        return newsRepository.getNewsHeadline(page)
    }
}