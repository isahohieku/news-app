package com.ohieku.newsapp.data.datasource

import com.ohieku.newsapp.data.api.ApiService
import com.ohieku.newsapp.data.entity.NewsResponse
import retrofit2.Response
import javax.inject.Inject

class NewsDataSourceImplementation @Inject constructor(
    private val apiService: ApiService
) : NewsDataSource {

    override suspend fun getNewsHeadline(country: String): Response<NewsResponse> {
        return apiService.getNewsHeadline(country)
    }
}