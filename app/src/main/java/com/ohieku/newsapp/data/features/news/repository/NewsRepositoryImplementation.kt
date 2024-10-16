package com.ohieku.newsapp.data.features.news.repository

import com.ohieku.newsapp.core.AppConstants
import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImplementation @Inject constructor(
    private val newsDataSource: NewsDataSource
): NewsRepository {
    override suspend fun getNewsHeadline(page: Int): ResourceState<NewsModel> =
        newsDataSource.getNewsHeadline(AppConstants.APP_DEFAULT_COUNTRY, page)
}