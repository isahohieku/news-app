package com.ohieku.newsapp.data.features.news.repository

import com.ohieku.newsapp.core.provider.LocaleProvider
import com.ohieku.newsapp.domain.common.ResourceState
import com.ohieku.newsapp.data.features.news.NewsApi
import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.data.features.news.model.ArticleDto
import com.ohieku.newsapp.data.features.news.model.NewsDto
import com.ohieku.newsapp.data.network.ApiResponseHandler
import com.ohieku.newsapp.domain.features.news.model.NewsCategory
import com.ohieku.newsapp.domain.features.news.model.NewsModel
import com.ohieku.newsapp.domain.features.news.model.SearchSort
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Response
import java.net.UnknownHostException
import java.util.Locale

class NewsRepositoryImplementationTest {

    private class FakeNewsApi : NewsApi {
        val calls = mutableListOf<Pair<String, Int>>()
        val categories = mutableListOf<String?>()
        var articlesByCountry: Map<String, Int> = mapOf("us" to 2)
        var respond: (country: String, page: Int) -> Response<NewsDto> = { country, _ ->
            Response.success(news(articlesByCountry[country] ?: 0))
        }

        override suspend fun getNewsHeadline(country: String, category: String?, page: Int): Response<NewsDto> {
            calls += country to page
            categories += category
            return respond(country, page)
        }

        val searches = mutableListOf<Triple<String, String?, Int>>()
        val sorts = mutableListOf<String>()

        override suspend fun searchNews(
            query: String,
            language: String?,
            page: Int,
            sortBy: String,
            pageSize: Int
        ): Response<NewsDto> {
            searches += Triple(query, language, page)
            sorts += sortBy
            return Response.success(news(1))
        }
    }

    companion object {
        private fun news(count: Int) = NewsDto(
            status = "ok",
            totalResults = count,
            articles = List(count) { ArticleDto(null, "title $it", null, null, null, null, null, null) }
        )
    }

    private val api = FakeNewsApi()
    private val repository = NewsRepositoryImplementation(
        NewsDataSource(api, ApiResponseHandler()),
        LocaleProvider(),
    )
    private lateinit var originalLocale: Locale

    @Before
    fun setUp() {
        originalLocale = Locale.getDefault()
    }

    @After
    fun tearDown() {
        Locale.setDefault(originalLocale)
    }

    private fun ResourceState<NewsModel>.articleCount(): Int {
        assertTrue("Expected Success but was $this", this is ResourceState.Success)
        return (this as ResourceState.Success).data.articles.size
    }

    @Test
    fun `us device requests lowercase us`() = runTest {
        Locale.setDefault(Locale.US)

        repository.getNewsHeadline(NewsCategory.Top, 1)

        assertEquals(listOf("us" to 1), api.calls)
    }

    @Test
    fun `device country is used when it has headlines`() = runTest {
        Locale.setDefault(Locale.GERMANY)
        api.articlesByCountry = mapOf("de" to 3, "us" to 2)

        val result = repository.getNewsHeadline(NewsCategory.Top, 1)

        assertEquals(3, result.articleCount())
        assertEquals(listOf("de" to 1), api.calls)
    }

    @Test
    fun `falls back to us when device country has no headlines and keeps it for later pages`() = runTest {
        Locale.setDefault(Locale("en", "NG"))

        val firstPage = repository.getNewsHeadline(NewsCategory.Top, 1)
        repository.getNewsHeadline(NewsCategory.Top, 2)

        assertEquals(2, firstPage.articleCount())
        assertEquals(listOf("ng" to 1, "us" to 1, "us" to 2), api.calls)
    }

    @Test
    fun `locale without a country uses us`() = runTest {
        Locale.setDefault(Locale("en"))

        repository.getNewsHeadline(NewsCategory.Top, 1)

        assertEquals(listOf("us" to 1), api.calls)
    }

    @Test
    fun `network error on device country is returned without falling back`() = runTest {
        Locale.setDefault(Locale.GERMANY)
        api.respond = { _, _ -> throw UnknownHostException() }

        val result = repository.getNewsHeadline(NewsCategory.Top, 1)

        assertTrue(result is ResourceState.Error)
        assertEquals(listOf("de" to 1), api.calls)
    }

    @Test
    fun `result limit response is treated as an empty page`() = runTest {
        Locale.setDefault(Locale.US)
        api.respond = { _, _ -> Response.error(426, "{}".toResponseBody("application/json".toMediaType())) }

        assertEquals(0, repository.getNewsHeadline(NewsCategory.Top, 6).articleCount())
    }

    @Test
    fun `category is sent as its api value and top sends none`() = runTest {
        Locale.setDefault(Locale.US)

        repository.getNewsHeadline(NewsCategory.Technology, 1)
        repository.getNewsHeadline(NewsCategory.Top, 1)

        assertEquals(listOf("technology", null), api.categories)
    }

    @Test
    fun `search uses the device language when NewsAPI supports it`() = runTest {
        Locale.setDefault(Locale.GERMANY)

        repository.searchNews("klima", SearchSort.Relevance, 2)

        assertEquals(listOf(Triple("klima", "de", 2)), api.searches)
    }

    @Test
    fun `search covers every language when the device language is unsupported`() = runTest {
        Locale.setDefault(Locale("yo", "NG"))

        repository.searchNews("news", SearchSort.Relevance, 1)

        assertEquals(listOf(Triple("news", null, 1)), api.searches)
    }

    @Test
    fun `hebrew is sent as he whichever code the locale reports`() = runTest {
        Locale.setDefault(Locale("he", "IL"))

        repository.searchNews("news", SearchSort.Relevance, 1)

        assertEquals("he", api.searches.single().second)
    }

    @Test
    fun `search sends the chosen sort order`() = runTest {
        Locale.setDefault(Locale.US)

        repository.searchNews("news", SearchSort.Relevance, 1)
        repository.searchNews("news", SearchSort.Latest, 1)

        assertEquals(listOf("relevancy", "publishedAt"), api.sorts)
    }
}
