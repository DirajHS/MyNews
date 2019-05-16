package com.diraj.mynews.data.factory

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.diraj.mynews.data.repository.TopHeadlinesRepository
import com.diraj.mynews.model.Articles
import javax.inject.Inject

class TopHeadlinesFactory @Inject constructor(private val topHeadlinesRepository: TopHeadlinesRepository) :
    DataSource.Factory<Int, Articles>() {

    val topHeadlinesLiveData = MutableLiveData<TopHeadlinesRepository>()

    override fun create(): DataSource<Int, Articles> {
        topHeadlinesLiveData.postValue(topHeadlinesRepository)
        return topHeadlinesRepository
    }

}