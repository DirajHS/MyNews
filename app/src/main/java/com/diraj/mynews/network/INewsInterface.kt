package com.diraj.mynews.network

import androidx.lifecycle.LiveData
import com.diraj.mynews.BuildConfig
import com.diraj.mynews.model.TopHeadlines
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query

interface INewsInterface {

    @Headers("X-Api-Key:" + BuildConfig.API_KEY)
    @GET("/v2/top-headlines")
    fun getTopHeadlines(
        @Query("country") country: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): LiveData<ApiResponse<TopHeadlines>>

    @Headers("X-Api-Key:" + BuildConfig.API_KEY)
    @GET("/v2/top-headlines")
    fun getTopHeadlinesWithCategory(
        @Query("country") country: String,
        @Query("category") category: String,
        @Query("pageSize") pageSize: Int,
        @Query("page") page: Int
    ): LiveData<ApiResponse<TopHeadlines>>
}