package com.diraj.mynews.data.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import com.diraj.mynews.AppExecutors
import com.diraj.mynews.db.TopHeadlinesDao
import com.diraj.mynews.model.Articles
import com.diraj.mynews.model.TopHeadlines
import com.diraj.mynews.network.*
import com.diraj.mynews.util.RateLimiter
import io.reactivex.Completable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class TopHeadlinesRepository @Inject constructor(
    private val appExecutors: AppExecutors,
    val newsInterface: INewsInterface,
    private val topHeadlinesDao: TopHeadlinesDao,
    private val repoListRateLimit: RateLimiter<String>,
    private val compositeDisposable: CompositeDisposable
) : PageKeyedDataSource<Int, Articles>() {

    companion object {
        var TAG = TopHeadlinesRepository::class.simpleName
    }

    val response: MutableLiveData<Resource<TopHeadlines>> = MutableLiveData()

    private var retryCompletable: Completable? = null

    private lateinit var category: String

    fun setCategory(category: String) {
        this.category = category
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Int, Articles>) {
        val rateLimiterKey = category + 1
        appExecutors.mainThread().execute {
            object : NetworkBoundResource<TopHeadlines, TopHeadlines>(appExecutors) {

                override fun saveCallResult(item: TopHeadlines) {
                    item.category = category
                    item.page = 0
                    topHeadlinesDao.insertTopHeadlines(topHeadlines = item)
                }

                override fun shouldFetch(data: TopHeadlines?): Boolean {
                    val shouldFetch = repoListRateLimit.shouldFetch(rateLimiterKey)
                    return shouldFetch || data == null
                }

                override fun loadFromDb(): LiveData<TopHeadlines> {
                    return topHeadlinesDao.getCachedTopHeadlines(category, 0)
                }

                override fun createCall(): LiveData<ApiResponse<TopHeadlines>> {
                    return newsInterface.getTopHeadlinesWithCategory("in", category, 20, 1)
                }

            }.asLiveData().observeForever { result ->
                when (result.status) {
                    Status.LOADING -> {
                        response.postValue(result)
                    }
                    Status.SUCCESS -> {
                        response.postValue(result)
                        callback.onResult(result.data!!.articles!!, 1, 2)
                    }
                    Status.ERROR -> {
                        response.postValue(result)
                        setRetry(Action { loadInitial(params, callback) })
                        repoListRateLimit.reset(rateLimiterKey)
                        Log.e(TAG, result.message)
                    }
                }
            }
        }

    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Int, Articles>) {
        val rateLimiterKey = category + params.key
        appExecutors.mainThread().execute {
            object : NetworkBoundResource<TopHeadlines, TopHeadlines>(appExecutors) {
                override fun saveCallResult(item: TopHeadlines) {
                    item.category = category
                    item.page = params.key
                    topHeadlinesDao.insertTopHeadlines(topHeadlines = item)
                }

                override fun shouldFetch(data: TopHeadlines?): Boolean {
                    val shouldFetch = repoListRateLimit.shouldFetch(rateLimiterKey)
                    return shouldFetch || data == null
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
                        callback.onResult(result.data!!.articles!!, params.key + 1)
                    }
                    Status.ERROR -> {
                        response.postValue(result)
                        setRetry(Action { loadAfter(params, callback) })
                        repoListRateLimit.reset(rateLimiterKey)
                        Log.e(TAG, result.message)
                    }
                }
            }
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Int, Articles>) {
    }

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(
                retryCompletable!!
                    .subscribeOn(Schedulers.io())
                    .subscribe()
            )
        }
    }

    private fun setRetry(action: Action?) {
        retryCompletable = if (action == null) null else Completable.fromAction(action)
    }

    fun onCleared() {
        compositeDisposable.dispose()
    }
}