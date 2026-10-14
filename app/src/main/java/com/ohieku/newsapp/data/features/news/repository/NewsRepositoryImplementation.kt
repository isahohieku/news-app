package com.ohieku.newsapp.data.features.news.repository

import com.ohieku.newsapp.core.AppConstants
import com.ohieku.newsapp.core.provider.LocaleProvider
import com.ohieku.newsapp.data.common.ResourceState
import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.data.network.ApiException
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import javax.inject.Inject

class NewsRepositoryImplementation @Inject constructor(
    private val newsDataSource: NewsDataSource,
    private val localeProvider: LocaleProvider,
): NewsRepository {

    // Country that returned headlines on the first page; later pages reuse it
    @Volatile
    private var resolvedCountry: String? = null

    override suspend fun getNewsHeadline(page: Int): ResourceState<NewsModel> {
        resolvedCountry?.let { return fetch(it, page) }

        // NewsAPI returns no headlines for many countries, so fall back to the default when the device's is empty
        val deviceCountry = localeProvider.getDefaultLocale().country.lowercase()
        if (deviceCountry.isNotBlank() && deviceCountry != AppConstants.APP_DEFAULT_COUNTRY) {
            val result = fetch(deviceCountry, page)
            if (result !is ResourceState.Success || result.data.articles.isNotEmpty()) {
                if (result is ResourceState.Success) resolvedCountry = deviceCountry
                return result
            }
        }

        val result = fetch(AppConstants.APP_DEFAULT_COUNTRY, page)
        if (result is ResourceState.Success) resolvedCountry = AppConstants.APP_DEFAULT_COUNTRY
        return result
    }

    private suspend fun fetch(country: String, page: Int): ResourceState<NewsModel> {
        val result = newsDataSource.getNewsHeadline(country, page)
        val error = (result as? ResourceState.Error)?.error as? ApiException

        // NewsAPI answers 426 once the account's result limit is passed; treat it as the end of the list
        return if (error?.statusCode == HTTP_MAX_RESULTS_REACHED) {
            ResourceState.Success(NewsModel(status = "ok", totalResults = 0, articles = emptyList()))
        } else {
            result
        }
    }

    companion object {
        private const val HTTP_MAX_RESULTS_REACHED = 426
    }
}
