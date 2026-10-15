package com.ohieku.newsapp.di

import android.content.Context
import android.content.SharedPreferences
import com.ohieku.newsapp.core.provider.LocaleProvider
import com.ohieku.newsapp.data.features.search.RecentSearchRepositoryImplementation
import com.ohieku.newsapp.domain.features.search.repository.RecentSearchRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkDao
import com.ohieku.newsapp.data.features.bookmarks.repository.BookmarkRepositoryImplementation
import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.data.features.news.repository.NewsRepositoryImplementation
import com.ohieku.newsapp.domain.features.bookmarks.repository.BookmarkRepository
import com.ohieku.newsapp.domain.features.news.repository.NewsRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    @[Provides Singleton]
    fun providesNewsRepository(newsDataSource: NewsDataSource, localeProvider: LocaleProvider): NewsRepository =
        NewsRepositoryImplementation(newsDataSource, localeProvider)

    @[Provides Singleton]
    fun providesBookmarkRepository(bookmarkDao: BookmarkDao): BookmarkRepository =
        BookmarkRepositoryImplementation(bookmarkDao)

    @[Provides Singleton]
    fun providesPreferences(@ApplicationContext context: Context): SharedPreferences =
        context.getSharedPreferences("central_news", Context.MODE_PRIVATE)

    @[Provides Singleton]
    fun providesRecentSearchRepository(preferences: SharedPreferences): RecentSearchRepository =
        RecentSearchRepositoryImplementation(preferences)
}
