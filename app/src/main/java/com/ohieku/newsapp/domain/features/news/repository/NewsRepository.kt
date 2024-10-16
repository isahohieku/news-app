package com.ohieku.newsapp.domain.features.news.repository

import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.domain.features.news.model.NewsModel

interface NewsRepository {
    suspend fun getNewsHeadline(page: Int): ResourceState<NewsModel>
}