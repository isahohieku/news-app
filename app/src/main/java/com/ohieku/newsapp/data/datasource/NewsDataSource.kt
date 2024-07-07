package com.ohieku.newsapp.data.datasource

import com.ohieku.newsapp.data.entity.NewsResponse
import retrofit2.Response

interface NewsDataSource {

    suspend fun getNewsHeadline(country: String): Response<NewsResponse>

}