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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader
import com.bumptech.glide.util.ViewPreloadSizeProvider
import com.diraj.mynews.R
import com.diraj.mynews.databinding.TopHeadlinesFragmentBinding
import com.diraj.mynews.di.GlideApp
import com.diraj.mynews.di.Injectable
import com.diraj.mynews.di.NewsViewModelFactory
import com.diraj.mynews.model.Articles
import com.diraj.mynews.network.Status
import com.diraj.mynews.ui.adapters.IOnClickInterface
import com.diraj.mynews.ui.adapters.TopHeadlinesAdapter
import timber.log.Timber
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

    private var isViewRestored: Int = -1

    private val PRELOAD_AHEAD_ITEMS = 5

    private var preloadSizeProvider: ViewPreloadSizeProvider<Articles>? = null
    lateinit var preloader: RecyclerViewPreloader<Articles>

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
    }

    override fun onItemClicked(t: Articles, view: View, transitionName: String) {
        newsClickInterface.onNewsArticleClick(t, view, transitionName)
    }

    override fun onResume() {
        super.onResume()

        initAdapter()

        viewModel.fetchNews(arguments!!.getString("category"))

        observeData()

        topHeadlinesBinding.animProgress.setAnimation(R.raw.news_animation)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isViewRestored =
            (topHeadlinesBinding.rvTopHeadlines.layoutManager!! as LinearLayoutManager)
                .findFirstCompletelyVisibleItemPosition()
    }

    private fun initAdapter() {
        adapter = TopHeadlinesAdapter(this, { viewModel.retry() }, context = context!!)
        preloadSizeProvider = ViewPreloadSizeProvider()
        preloader = RecyclerViewPreloader(GlideApp.with(this), adapter, preloadSizeProvider!!,
            PRELOAD_AHEAD_ITEMS)

        topHeadlinesBinding.rvTopHeadlines.addOnScrollListener(preloader)
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

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0 && isViewRestored > 0) {
                    scrollToPosition(isViewRestored)
                }
            }
        })

        (topHeadlinesBinding.rvTopHeadlines.layoutManager as LinearLayoutManager).scrollToPosition(25)
        viewModel.statusResource.observe(this, Observer { status ->
            topHeadlinesBinding.animProgress.visibility =
                if (viewModel.listIsEmpty() && status.status == Status.LOADING) View.VISIBLE else View.GONE
            topHeadlinesBinding.tvError.visibility =
                if (viewModel.listIsEmpty() && status.status == Status.ERROR) View.VISIBLE else View.GONE
            if (!viewModel.listIsEmpty()) adapter.setNewNetworkState(status)
        })
    }

    private fun scrollToPosition(pos: Int) {
        Timber.d("scrolling to position $isViewRestored")
        (topHeadlinesBinding.rvTopHeadlines.layoutManager as LinearLayoutManager).scrollToPositionWithOffset(pos, 0)
    }

    fun setNewsClickListener(newsClickInterface: IOnNewsClickInterface) {
        this.newsClickInterface = newsClickInterface
    }

}
