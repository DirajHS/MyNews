package com.diraj.mynews.db

import androidx.room.TypeConverter
import com.diraj.mynews.model.Articles
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

object ArticlesConverter {
    private val gson: Gson = Gson()

    @TypeConverter
    @JvmStatic
    fun stringArticlesToList(stringArticles: String?): List<Articles> {
        if (stringArticles == null) {
            return Collections.emptyList()
        }

        val articlesListType = object : TypeToken<List<Articles>>() {}.type

        return gson.fromJson(stringArticles, articlesListType)
    }

    @TypeConverter
    @JvmStatic
    fun listArticlesToString(articlesList: List<Articles>): String {
        return gson.toJson(articlesList)
    }
}