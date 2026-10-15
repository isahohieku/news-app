package com.ohieku.newsapp.domain.features.search.repository

import kotlinx.coroutines.flow.Flow

interface RecentSearchRepository {
    /** Most recent first, without duplicates. */
    fun observeRecentSearches(): Flow<List<String>>

    suspend fun addRecentSearch(query: String)

    suspend fun clearRecentSearches()
}
