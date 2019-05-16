package com.diraj.mynews.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.diraj.mynews.db.ArticlesConverter

@Entity(tableName = "topheadlines", primaryKeys = ["status"])
@TypeConverters(ArticlesConverter::class)
data class TopHeadlines(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "int")
    var int: Long = 0,
    @ColumnInfo(name = "status")
    var status: String = "",
    @ColumnInfo(name = "totalResults")
    var totalResults: Int? = 0,
    @ColumnInfo(name = "articles")
    var articles: List<Articles>? = null
) {

}