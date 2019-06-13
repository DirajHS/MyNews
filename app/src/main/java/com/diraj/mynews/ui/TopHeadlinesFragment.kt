package com.diraj.mynews.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.diraj.mynews.R
import com.diraj.mynews.databinding.TopHeadlinesFragmentBinding
import com.diraj.mynews.di.Injectable
import com.diraj.mynews.di.NewsViewModelFactory
import com.diraj.mynews.model.Articles
import com.diraj.mynews.ui.adapters.IOnClickInterface
import com.diraj.mynews.ui.adapters.TopHeadlinesAdapter
import javax.inject.Inject

class TopHeadlinesFragment : Fragment(), Injectable, IOnClickInterface<Articles> {

    @Inject
    lateinit var viewModelFactory: NewsViewModelFactory

    companion object {
        fun newInstance(category: String): TopHeadlinesFragment {
            val topHeadlinesFragment = TopHeadlinesFragment()
            val args = Bundle()
            args.putString("category", category)
            topHeadlinesFragment.arguments = args
            return topHeadlinesFragment
        }
    }

    private lateinit var viewModel: TopHeadlinesViewModel

    private lateinit var topHeadlinesBinding: TopHeadlinesFragmentBinding

    private lateinit var adapter: TopHeadlinesAdapter

    private lateinit var newsClickInterface: IOnNewsClickInterface

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        topHeadlinesBinding = DataBindingUtil.inflate(inflater, R.layout.top_headlines_fragment, container, false)
        return topHeadlinesBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(TopHeadlinesViewModel::class.java)

        (activity as IActionBarInterface).setUpActionBar()

        initAdapter()

        viewModel.fetchNews(arguments!!.getString("category"))

        observeData()
    }

    override fun onItemClicked(t: Articles, view: View, transitionName: String) {
        newsClickInterface.onNewsArticleClick(t, view, transitionName)
    }

    private fun initAdapter() {
        adapter = TopHeadlinesAdapter(this) { viewModel.retry() }
        topHeadlinesBinding.rvTopHeadlines.layoutManager = LinearLayoutManager(context!!.applicationContext)
        topHeadlinesBinding.rvTopHeadlines.adapter = this.adapter

        topHeadlinesBinding.rvTopHeadlines.addItemDecoration(
            DividerItemDecoration(
                this.context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun observeData() {
        viewModel.resource.observe(this, Observer { articles ->
            adapter.submitList(articles)
        })

        viewModel.statusResource.observe(this, Observer { status ->
            Log.d(TopHeadlinesFragment::class.simpleName, "${status.message}")
            if (!viewModel.listIsEmpty()) adapter.setNewNetworkState(status)
        })
    }

    fun setNewsClickListener(newsClickInterface: IOnNewsClickInterface) {
        this.newsClickInterface = newsClickInterface
    }

}
