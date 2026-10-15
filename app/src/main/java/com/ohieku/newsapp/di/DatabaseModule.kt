package com.ohieku.newsapp.di

import android.content.Context
import androidx.room.Room
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkDao
import com.ohieku.newsapp.data.local.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @[Provides Singleton]
    fun providesDatabase(@ApplicationContext context: Context): NewsDatabase =
        Room.databaseBuilder(context, NewsDatabase::class.java, "news.db").build()

    @Provides
    fun providesBookmarkDao(database: NewsDatabase): BookmarkDao = database.bookmarkDao()
}
