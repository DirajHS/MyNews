package com.diraj.mynews.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.diraj.mynews.AppExecutors
import com.diraj.mynews.db.TopHeadlinesDao
import com.diraj.mynews.model.Articles
import com.diraj.mynews.model.TopHeadlines
import com.diraj.mynews.network.*
import com.diraj.mynews.util.RateLimiter
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class TopHeadlinesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    val newsInterface: INewsInterface,
    private val topHeadlinesDao: TopHeadlinesDao
) : PageKeyedDataSource<Int, Articles>() {


    val response: MutableLiveData<Resource<TopHeadlines>> = MutableLiveData()

    private val repoListRateLimit = RateLimiter<String>(10, TimeUnit.MINUTES)

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Articles>) {

        appExecutors.mainThread().execute {
            object : NetworkBoundResource<TopHeadlines, TopHeadlines>(appExecutors) {
                override fun saveCallResult(item: TopHeadlines) {
                    topHeadlinesDao.insertTopHeadlines(topHeadlines = item)
                }

                override fun shouldFetch(data: TopHeadlines?): Boolean {
                    return data == null || repoListRateLimit.shouldFetch("topHeadlines")
                }

                override fun loadFromDb(): LiveData<TopHeadlines> {
                    return topHeadlinesDao.getCachedTopHeadlines()
                }

                override fun createCall(): LiveData<ApiResponse<TopHeadlines>> {
                    return newsInterface.getTopHeadlines("in", 20, 0)
                }

            }.asLiveData().observeForever { result ->
                when (result.status) {
                    Status.LOADING -> {
                        response.postValue(result)
                    }
                    Status.SUCCESS -> {
                        response.postValue(result)
                        callback.onResult(result.data!!.articles!!, 0, 1)
                    }
                    Status.ERROR -> {
                        response.postValue(result)
                    }
                }
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Articles>) {
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Articles>) {
    }

}