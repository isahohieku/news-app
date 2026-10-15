package com.ohieku.newsapp.data.features.news

import com.ohieku.newsapp.data.features.news.model.NewsDto
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface NewsApi {
    @GET("top-headlines")
    suspend fun getNewsHeadline(
        @Query("country") country: String,
        @Query("category") category: String?,
        @Query("page") page: Int,
    ): Response<NewsDto>


}