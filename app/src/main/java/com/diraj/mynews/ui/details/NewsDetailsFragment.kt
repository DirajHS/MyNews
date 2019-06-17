package com.diraj.mynews.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.transition.TransitionInflater
import com.diraj.mynews.BR
import com.diraj.mynews.R
import com.diraj.mynews.databinding.NewsDetailsFragmentBinding
import com.diraj.mynews.model.Articles
import com.diraj.mynews.ui.IActionBarInterface

class NewsDetailsFragment : Fragment() {

    private lateinit var newsLayoutBinding: NewsDetailsFragmentBinding

    companion object {
        fun newInstance() = NewsDetailsFragment()
    }

    private lateinit var viewModel: NewsDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newsLayoutBinding =  DataBindingUtil.inflate(inflater, R.layout.news_details_fragment, container, false)
        return newsLayoutBinding.root
    }
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsDetailsViewModel::class.java)

        (activity as IActionBarInterface).setUpToolbar(newsLayoutBinding.collapsingToolbar, newsLayoutBinding.toolbar)

        val article = arguments!!.getSerializable("article") as Articles
        newsLayoutBinding.setVariable(BR.article, article)
    }

}
