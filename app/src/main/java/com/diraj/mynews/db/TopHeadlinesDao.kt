package com.diraj.mynews.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diraj.mynews.model.TopHeadlines

@Dao
interface TopHeadlinesDao {

    @Query("SELECT * FROM topheadlines")
    fun getCachedTopHeadlines(): LiveData<TopHeadlines>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadlines(topHeadlines: TopHeadlines)
}