package com.diraj.mynews.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.TypeConverters
import com.diraj.mynews.db.ArticlesConverter
import com.google.gson.annotations.SerializedName

@Entity(tableName = "topheadlines", primaryKeys = ["category", "page"])
@TypeConverters(ArticlesConverter::class)
data class TopHeadlines(
    @SerializedName("status")
    @ColumnInfo(name = "status")
    var status: String = "",
    @SerializedName("totalResults")
    @ColumnInfo(name = "totalResults")
    var totalResults: Int? = 0,
    @SerializedName("articles")
    @ColumnInfo(name = "articles")
    var articles: List<Articles>? = null
) {
    @SerializedName("category")
    @ColumnInfo(name = "category")
    var category: String = ""
    @SerializedName("page")
    @ColumnInfo(name = "page")
    var page: Int = 0
}