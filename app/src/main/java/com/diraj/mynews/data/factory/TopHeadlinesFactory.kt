package com.diraj.mynews.data.factory

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.diraj.mynews.AppExecutors
import com.diraj.mynews.data.repository.TopHeadlinesRepository
import com.diraj.mynews.db.TopHeadlinesDao
import com.diraj.mynews.model.Articles
import com.diraj.mynews.network.INewsInterface
import com.diraj.mynews.util.RateLimiter
import io.reactivex.disposables.CompositeDisposable
import javax.inject.Inject

class TopHeadlinesFactory @Inject constructor(
    private val appExecutors: AppExecutors,
    val newsInterface: INewsInterface,
    private val topHeadlinesDao: TopHeadlinesDao,
    private val repoListRateLimit: RateLimiter<String>,
    private val compositeDisposable: CompositeDisposable
) :
    DataSource.Factory<Int, Articles>() {

    val topHeadlinesLiveData = MutableLiveData<TopHeadlinesRepository>()

    private lateinit var category: String

    fun setCategory(category: String) {
        this.category = category
    }

    override fun create(): DataSource<Int, Articles> {
        val topHeadlinesRepository =
            TopHeadlinesRepository(appExecutors, newsInterface, topHeadlinesDao, repoListRateLimit, compositeDisposable)
        topHeadlinesRepository.setCategory(category)
        topHeadlinesLiveData.postValue(topHeadlinesRepository)
        return topHeadlinesRepository
    }

}