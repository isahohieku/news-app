package com.ohieku.newsapp.fakes

import com.ohieku.newsapp.domain.features.search.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

class FakeRecentSearchRepository : RecentSearchRepository {
    val searches = MutableStateFlow<List<String>>(emptyList())

    override fun observeRecentSearches(): Flow<List<String>> = searches

    override suspend fun addRecentSearch(query: String) {
        searches.value = listOf(query) + searches.value.filterNot { it == query }
    }

    override suspend fun clearRecentSearches() {
        searches.value = emptyList()
    }
}
