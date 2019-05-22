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
import javax.inject.Inject

class TopHeadlinesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    val newsInterface: INewsInterface,
    private val topHeadlinesDao: TopHeadlinesDao,
    private val repoListRateLimit: RateLimiter<String>
) : PageKeyedDataSource<Int, Articles>() {

    private var fetchedItemsCount = 0

    val response: MutableLiveData<Resource<TopHeadlines>> = MutableLiveData()

    private lateinit var category: String

    fun setCategory(category: String) {
        this.category = category
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Articles>) {
        appExecutors.mainThread().execute {
            object : NetworkBoundResource<TopHeadlines, TopHeadlines>(appExecutors) {

                override fun saveCallResult(item: TopHeadlines) {
                    item.category = category
                    item.page = 0
                    topHeadlinesDao.insertTopHeadlines(topHeadlines = item)
                }

                override fun shouldFetch(data: TopHeadlines?): Boolean {
                    return repoListRateLimit.shouldFetch(category) || data == null
                }

                override fun loadFromDb(): LiveData<TopHeadlines> {
                    return topHeadlinesDao.getCachedTopHeadlines(category, 0)
                }

                override fun createCall(): LiveData<ApiResponse<TopHeadlines>> {
                    return newsInterface.getTopHeadlinesWithCategory("in", category, 20, 0)
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
        appExecutors.mainThread().execute {
            object : NetworkBoundResource<TopHeadlines, TopHeadlines>(appExecutors) {
                override fun saveCallResult(item: TopHeadlines) {
                    item.category = category
                    item.page = params.key
                    topHeadlinesDao.insertTopHeadlines(topHeadlines = item)
                }

                override fun shouldFetch(data: TopHeadlines?): Boolean {
                    return repoListRateLimit.shouldFetch(category) || data == null
                }

                override fun loadFromDb(): LiveData<TopHeadlines> {
                    return topHeadlinesDao.getCachedTopHeadlines(category, params.key)
                }

                override fun createCall(): LiveData<ApiResponse<TopHeadlines>> {
                    return newsInterface.getTopHeadlinesWithCategory("in", category, 20, params.key)
                }

            }.asLiveData().observeForever { result ->
                when (result.status) {
                    Status.LOADING -> {
                        response.postValue(result)
                    }
                    Status.SUCCESS -> {
                        response.postValue(result)
                        fetchedItemsCount += result.data!!.articles!!.size
                        if (fetchedItemsCount < result.data.totalResults!!) {
                            callback.onResult(result.data.articles!!, params.key + 1)
                        } else {
                            callback.onResult(result.data.articles!!, null)
                        }
                    }
                    Status.ERROR -> {
                        response.postValue(result)
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Articles>) {
    }

}