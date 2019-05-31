package com.diraj.mynews.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.diraj.mynews.data.factory.TopHeadlinesFactory
import com.diraj.mynews.data.repository.TopHeadlinesRepository
import com.diraj.mynews.model.Articles
import com.diraj.mynews.model.TopHeadlines
import com.diraj.mynews.network.Resource
import javax.inject.Inject

class TopHeadlinesViewModel @Inject constructor(
    private var topHeadlinesFactory: TopHeadlinesFactory
) : ViewModel() {

    var statusResource: LiveData<Resource<TopHeadlines>>
    lateinit var resource: LiveData<PagedList<Articles>>
    private val pagedListConfig: PagedList.Config

    init {
        statusResource =
            Transformations.switchMap(topHeadlinesFactory.topHeadlinesLiveData) { input: TopHeadlinesRepository? ->
                input!!.response
            }
        pagedListConfig = PagedList.Config.Builder()
            .setEnablePlaceholders(false)
            .setPageSize(20)
            .build()
    }

    fun listIsEmpty(): Boolean {
        return resource.value?.isEmpty() ?: true
    }

    fun fetchNews(category: String?) {

        topHeadlinesFactory.setCategory(category!!)

        resource = LivePagedListBuilder(
            topHeadlinesFactory,
            pagedListConfig
        ).build()
    }

    fun retry () {
        topHeadlinesFactory.topHeadlinesLiveData.value?.retry()
    }

    override fun onCleared() {
        super.onCleared()
        topHeadlinesFactory.topHeadlinesLiveData.value?.onCleared()
    }
}
