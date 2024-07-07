package com.ohieku.newsapp.ui.repository

import com.ohieku.newsapp.data.datasource.NewsDataSource
import com.ohieku.newsapp.data.entity.NewsResponse
import com.ohieku.utilities.ResourceState
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import javax.inject.Inject

class NewsRepository @Inject constructor(
    private val newsDataSource: NewsDataSource
) {

    suspend fun getNewsHeadline(country: String): Flow<ResourceState<NewsResponse>> {
        return flow<ResourceState<NewsResponse>> {
            emit(ResourceState.Loading())
            val response = newsDataSource.getNewsHeadline(country)

            if(response.isSuccessful && response.body() !=null) {
                emit(ResourceState.Success(response.body()!!))
            } else {
                emit(ResourceState.Error("Error fetching news data"))
            }
        }.catch { e ->
            emit(ResourceState.Error(e?.localizedMessage ?: "Some error in flow"))
        }
    }

}