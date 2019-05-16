package com.diraj.mynews.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.diraj.mynews.model.TopHeadlines

@Database(
    entities = [TopHeadlines::class],
    version = 1,
    exportSchema = false
)
abstract class NewsDB : RoomDatabase() {

    abstract fun topHeadlinesDao(): TopHeadlinesDao
}
