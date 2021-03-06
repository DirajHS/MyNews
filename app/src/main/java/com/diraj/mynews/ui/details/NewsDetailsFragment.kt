package com.diraj.mynews.ui.details

import android.app.SharedElementCallback
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.URLSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.transition.Transition
import androidx.transition.TransitionInflater
import androidx.transition.TransitionListenerAdapter
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.diraj.mynews.BR
import com.diraj.mynews.R
import com.diraj.mynews.databinding.NewsDetailsFragmentBinding
import com.diraj.mynews.di.GlideApp
import com.diraj.mynews.model.Articles
import com.google.android.material.appbar.AppBarLayout
import timber.log.Timber

class NewsDetailsFragment : Fragment() {

    private lateinit var newsLayoutBinding: NewsDetailsFragmentBinding

    private lateinit var article: Articles

    companion object {
        fun newInstance() = NewsDetailsFragment()
    }

    private lateinit var viewModel: NewsDetailsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        postponeEnterTransition()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        newsLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.news_details_fragment, container, false)
        return newsLayoutBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(NewsDetailsViewModel::class.java)

        article = arguments!!.getSerializable("article") as Articles
        if(article.urlToImage != null) {
            newsLayoutBinding.toolbarImage.load(article.urlToImage!!, loadOnlyFromCache = true) {
                showNewsDetails()
            }
        } else {
            showNewsDetails()
        }

        setUpToolbar()
    }

    private fun showNewsDetails() {
        sharedElementEnterTransition =
            TransitionInflater.from(context).inflateTransition(android.R.transition.move)
        (sharedElementEnterTransition as Transition).addListener(object :
            TransitionListenerAdapter() {
            override fun onTransitionEnd(transition: Transition) {
                super.onTransitionEnd(transition)
                article.urlToImage?.let { newsLayoutBinding.toolbarImage.load(it) }
            }
        })
        startPostponedEnterTransition()
        newsLayoutBinding.setVariable(BR.article, article)
        addUrl()
    }

    private fun setUpToolbar() {
        newsLayoutBinding.appBar.addOnOffsetChangedListener(object : AppBarLayout.OnOffsetChangedListener {
            var isShow = true
            var scrollRange = -1

            override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.totalScrollRange
                }
                if (scrollRange + verticalOffset == 0) {
                    newsLayoutBinding.collapsingToolbar.title = getString(R.string.app_name)
                    isShow = true
                } else if (isShow) {
                    newsLayoutBinding.collapsingToolbar.title = " "
                    isShow = false
                }
            }
        })
    }

    fun ImageView.load(url: String, loadOnlyFromCache: Boolean = false, onLoadingFinished: () -> Unit = {}) {
        val listener = object : RequestListener<Drawable> {
            override fun onLoadFailed(
                e: GlideException?, model: Any?, target: Target<Drawable>?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadingFinished()
                return false
            }

            override fun onResourceReady(
                resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?,
                isFirstResource: Boolean
            ): Boolean {
                onLoadingFinished()
                return false
            }
        }
        val requestOptions = RequestOptions.placeholderOf(R.drawable.placeholder)
            .dontTransform()
            .onlyRetrieveFromCache(loadOnlyFromCache)
        GlideApp.with(this)
            .load(url)
            .apply(requestOptions)
            .listener(listener)
            .into(this)
    }

    private fun addUrl() {

        if (article.content == null)
            return

        val patternStart = "[+"
        val patternEnd = "]"
        val startIndex = article.content!!.indexOf(patternStart)
        val endIndex = article.content!!.indexOf(patternEnd, startIndex)

        val spannableString = SpannableString(article.content)
        val url = article.url
        if((startIndex in 1 until endIndex) && (endIndex < article.content!!.length))
            spannableString.setSpan(URLSpan(url), startIndex, endIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        newsLayoutBinding.tvContent.text = spannableString
        newsLayoutBinding.tvContent.movementMethod = LinkMovementMethod.getInstance()
    }

}
