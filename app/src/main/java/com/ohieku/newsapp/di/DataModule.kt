package com.ohieku.newsapp.di

import com.ohieku.newsapp.data.features.news.datasource.NewsDataSource
import com.ohieku.newsapp.data.features.news.repository.NewsRepositoryImplementation
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
    fun providesNewsRepository(newsDataSource: NewsDataSource): NewsRepository =
        NewsRepositoryImplementation(newsDataSource)
}