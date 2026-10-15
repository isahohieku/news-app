package com.ohieku.newsapp.data.features.bookmarks.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val url: String,
    val title: String?,
    val description: String?,
    val urlToImage: String?,
    val publishedAt: String?,
    val content: String?,
    val author: String?,
    val sourceId: String?,
    val sourceName: String?,
    val savedAt: Long,
)
