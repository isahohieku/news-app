package com.ohieku.newsapp.data.api

import com.ohieku.newsapp.BuildConfig
import com.ohieku.newsapp.data.entity.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query


interface ApiService {

    @GET("top-headlines")
    suspend fun getNewsHeadline(
        @Query("country") country: String,
        @Query("apiKey") apiKey: String = BuildConfig.API_KEY
    ): Response<NewsResponse>


}