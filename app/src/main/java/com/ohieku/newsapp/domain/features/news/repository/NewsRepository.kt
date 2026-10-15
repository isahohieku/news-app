package com.ohieku.newsapp.domain.features.news.repository

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.model.SearchSort

interface NewsRepository {
    suspend fun getNewsHeadline(category: NewsCategory, page: Int): ResourceState<NewsModel>

    suspend fun searchNews(query: String, sort: SearchSort, page: Int): ResourceState<NewsModel>
}