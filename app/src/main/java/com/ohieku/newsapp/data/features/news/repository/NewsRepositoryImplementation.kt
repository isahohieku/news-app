package com.ohieku.newsapp.data.features.news.repository

import com.ohieku.newsapp.core.provider.LocaleProvider
import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImplementation @Inject constructor(
    private val newsDataSource: NewsDataSource,
    private val localeProvider: LocaleProvider,
): NewsRepository {
    override suspend fun getNewsHeadline(page: Int): ResourceState<NewsModel> {
        val locale = localeProvider.getDefaultLocale()
        return newsDataSource.getNewsHeadline(locale.country, locale.language, page)
    }
}