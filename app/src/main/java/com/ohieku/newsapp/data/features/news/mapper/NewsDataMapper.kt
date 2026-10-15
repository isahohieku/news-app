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
            title = articleDto.title?.let { stripSourceSuffix(it, articleDto.source?.name) },
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

    private val SUFFIX_SEPARATOR = Regex("""\s+[-–—|]\s+""")

    /**
     * Top headlines arrive as "Headline - Publisher". Drops that trailing publisher, since the card shows the
     * source separately. A trailing segment is only treated as a publisher when it names the source or reads
     * like a name (at most five words, each capitalised), so "Apple unveils M5 - here's what's new" survives.
     */
    fun stripSourceSuffix(title: String, sourceName: String?): String {
        val match = SUFFIX_SEPARATOR.findAll(title).lastOrNull() ?: return title
        val headline = title.substring(0, match.range.first).trim()
        val suffix = title.substring(match.range.last + 1).trim()
        if (headline.isEmpty() || suffix.isEmpty()) return title

        val namesSource = sourceName != null && suffix.equals(sourceName.trim(), ignoreCase = true)
        val words = suffix.split(' ').filter { it.isNotEmpty() }
        val readsLikeName = words.size <= 5 && words.all { it.first().isUpperCase() || it.first().isDigit() }
        return if (namesSource || readsLikeName) headline else title
    }
}
