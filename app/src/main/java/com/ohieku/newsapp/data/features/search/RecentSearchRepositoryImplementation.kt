package com.ohieku.newsapp.data.features.search

import android.content.SharedPreferences
import com.ohieku.newsapp.domain.features.search.repository.RecentSearchRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/** Keeps the last few searches in SharedPreferences as one newline-separated value. */
class RecentSearchRepositoryImplementation @Inject constructor(
    private val preferences: SharedPreferences,
) : RecentSearchRepository {

    private val recent = MutableStateFlow(read())

    override fun observeRecentSearches(): Flow<List<String>> = recent.asStateFlow()

    override suspend fun addRecentSearch(query: String) {
        val trimmed = query.trim().replace('\n', ' ')
        if (trimmed.isEmpty()) return
        val updated = (listOf(trimmed) + recent.value.filterNot { it.equals(trimmed, ignoreCase = true) })
            .take(MAX_RECENT_SEARCHES)
        write(updated)
    }

    override suspend fun clearRecentSearches() {
        write(emptyList())
    }

    private fun read(): List<String> =
        preferences.getString(KEY, null)?.split('\n')?.filter { it.isNotBlank() }.orEmpty()

    private fun write(searches: List<String>) {
        preferences.edit().putString(KEY, searches.joinToString("\n")).apply()
        recent.value = searches
    }

    companion object {
        private const val KEY = "recent_searches"
        const val MAX_RECENT_SEARCHES = 5
    }
}
