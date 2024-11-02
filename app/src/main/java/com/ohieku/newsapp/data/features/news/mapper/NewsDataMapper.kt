package com.ohieku.newsapp.data.features.news.mapper

import com.ohieku.newsapp.data.features.news.model.ArticleDto
import com.ohieku.newsapp.data.features.news.model.NewsDto
import com.ohieku.newsapp.data.features.news.model.SourceDto
import com.ohieku.newsapp.domain.features.news.model.Article
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.model.Source

object NewsDataMapper {
    val newsDtoToDomainModel: (NewsDto) -> NewsModel = { newsDto ->
        NewsModel(
            status = newsDto.status,
            totalResults = newsDto.totalResults,
            articles = newsDto.articles.map { articleDtoToDomainModel(it) }
        )
    }

    val articleDtoToDomainModel: (ArticleDto) -> Article = {articleDto ->
        Article(
            author = articleDto.author,
            title = articleDto.title,
            description = articleDto.description,
            url = articleDto.url,
            urlToImage = articleDto.urlToImage,
            publishedAt = articleDto.publishedAt,
            content = articleDto.content,
            source = articleDto.source?.let { source -> sourceDtoToDomainModel(source) },
        )
    }

    val sourceDtoToDomainModel: (SourceDto) -> Source = { sourceDto ->
        Source(
            id = sourceDto.id,
            name = sourceDto.name
        )
    }
}