package com.ohieku.newsapp.data.features.news.mapper

import com.ohieku.newsapp.data.features.news.model.NewsDto
import com.ohieku.newsapp.domain.features.news.model.NewsModel

object NewsDataMapper {
    val newsDtoToDomainModel: (NewsDto) -> NewsModel = {
        NewsModel(
            status = it.status,
            totalResults = it.totalResults,
            articles = it.articles,
        )
    }
}