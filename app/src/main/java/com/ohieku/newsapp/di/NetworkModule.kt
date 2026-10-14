package com.ohieku.newsapp.di

import com.ohieku.newsapp.BuildConfig
import com.ohieku.newsapp.core.AppConstants
import com.ohieku.newsapp.data.features.news.NewsApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val API_KEY_HEADER = "X-Api-Key"

    @[Provides Singleton]
    fun providesRetrofit(): Retrofit {
        val httpClient = OkHttpClient().newBuilder().apply {
            addInterceptor { chain ->
                val request = chain.request().newBuilder()
                    .header(API_KEY_HEADER, BuildConfig.API_KEY)
                    .build()
                chain.proceed(request)
            }

            if (BuildConfig.DEBUG) {
                addInterceptor(
                    HttpLoggingInterceptor().apply {
                        level = HttpLoggingInterceptor.Level.BASIC
                        redactHeader(API_KEY_HEADER)
                    }
                )
            }
        }

        httpClient.apply {
            readTimeout(60, TimeUnit.SECONDS)
        }

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        return Retrofit.Builder()
            .baseUrl(AppConstants.APP_BASE_URL)
            .client(httpClient.build())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @[Provides Singleton]
    fun providesApiService(retrofit: Retrofit): NewsApi {
        return retrofit.create((NewsApi::class.java))
    }
}