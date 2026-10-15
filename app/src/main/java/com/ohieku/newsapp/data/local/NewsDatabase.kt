package com.ohieku.newsapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkDao
import com.ohieku.newsapp.data.features.bookmarks.local.BookmarkEntity

@Database(entities = [BookmarkEntity::class], version = 1, exportSchema = false)
abstract class NewsDatabase : RoomDatabase() {
    abstract fun bookmarkDao(): BookmarkDao
}
