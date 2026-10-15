package com.ohieku.newsapp.domain.features.news.model

/** How /everything orders results; relevance is the default, since newest-first surfaces passing mentions. */
enum class SearchSort(val apiValue: String) {
    Relevance("relevancy"),
    Latest("publishedAt"),
}
