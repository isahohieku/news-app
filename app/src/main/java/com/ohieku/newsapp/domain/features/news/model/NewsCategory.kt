package com.ohieku.newsapp.domain.features.news.model

/** NewsAPI top-headline categories; [Top] sends no category so the API returns every headline. */
enum class NewsCategory(val apiValue: String?) {
    Top(null),
    Business("business"),
    Entertainment("entertainment"),
    Health("health"),
    Science("science"),
    Sports("sports"),
    Technology("technology"),
}
