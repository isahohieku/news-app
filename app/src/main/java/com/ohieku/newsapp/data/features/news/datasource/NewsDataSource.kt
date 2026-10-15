package com.ohieku.newsapp.data.features.news.datasource

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.features.news.NewsApi
import com.ohieku.newsapp.data.features.news.mapper.NewsDataMapper.newsDtoToDomainModel
import com.ohieku.newsapp.data.network.ApiResponseHandler
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import javax.inject.Inject

class NewsDataSource @Inject constructor(
    private val newsApi: NewsApi,
    private val apiResponseHandler: ApiResponseHandler,
) {

    suspend fun getNewsHeadline(country: String, category: String?, page: Int): ResourceState<NewsModel> {
        return apiResponseHandler.handleResponseMapping(
            call = { newsApi.getNewsHeadline(country, category, page) },
            mapper = newsDtoToDomainModel,
        )
    }

    suspend fun searchNews(query: String, language: String?, page: Int): ResourceState<NewsModel> {
        return apiResponseHandler.handleResponseMapping(
            call = { newsApi.searchNews(query, language, page) },
            mapper = newsDtoToDomainModel,
        )
    }
}