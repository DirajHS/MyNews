package com.diraj.mynews.ui

import android.os.Bundle
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
import com.diraj.mynews.ui.adapters.TopHeadlinesAdapter
import javax.inject.Inject

class TopHeadlinesFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: NewsViewModelFactory

    companion object {
        fun newInstance() = TopHeadlinesFragment()
    }

    private lateinit var viewModel: TopHeadlinesViewModel

    private lateinit var topHeadlinesBinding: TopHeadlinesFragmentBinding

    private lateinit var adapter: TopHeadlinesAdapter

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

        initAdapter()

        observeData()
    }

    private fun initAdapter() {
        adapter = TopHeadlinesAdapter()
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
            if (!viewModel.listIsEmpty()) adapter.setNewNetworkState(status)
        })
    }

}
