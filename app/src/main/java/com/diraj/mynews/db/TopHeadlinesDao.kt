package com.diraj.mynews.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.diraj.mynews.model.TopHeadlines

@Dao
interface TopHeadlinesDao {

    @Query("SELECT * FROM topheadlines WHERE category = :category AND page = :page")
    fun getCachedTopHeadlines(category :String, page:Int): LiveData<TopHeadlines>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTopHeadlines(topHeadlines: TopHeadlines) : Long
}