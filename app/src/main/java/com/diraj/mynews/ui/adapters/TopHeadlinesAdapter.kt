package com.diraj.mynews.ui.adapters

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.ListPreloader
import com.bumptech.glide.RequestBuilder
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade
import com.diraj.mynews.R
import com.diraj.mynews.databinding.ArticlesItemLayoutBinding
import com.diraj.mynews.databinding.NetworkItemBinding
import com.diraj.mynews.di.GlideApp
import com.diraj.mynews.di.GlideRequest
import com.diraj.mynews.di.GlideRequests
import com.diraj.mynews.model.Articles
import java.util.Collections.emptyList
import java.util.Collections.singletonList

class TopHeadlinesAdapter(
    private val itemClickCallback: IOnClickInterface<Articles>,
    private val retry: () -> Unit,
    glideRequests: GlideRequests,
    private val context: Context
) : BaseListAdapter<Articles, RecyclerView.ViewHolder>(TopHeadlinesDiffCallback),
    ListPreloader.PreloadModelProvider<Articles> {

    private var fullRequest: GlideRequest<Drawable>? = null
    private var thumbRequest: GlideRequest<Drawable>? = null

    init {
        fullRequest = glideRequests
            .asDrawable()
            .fitCenter()
            .placeholder(ColorDrawable(Color.GRAY))

        thumbRequest = glideRequests
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.DATA)
            .override(250, 250)
            .transition(withCrossFade())
    }

    private val TYPE_PROGRESS = 0
    private val TYPE_ITEM = 1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_PROGRESS -> {
                val networkBinding: NetworkItemBinding = NetworkItemBinding.inflate(layoutInflater, parent, false)
                NetworkStateItemViewHolder(networkBinding, retry = retry)
            }
            else -> {
                val articleBinding: ArticlesItemLayoutBinding =
                    ArticlesItemLayoutBinding.inflate(layoutInflater, parent, false)
                ArticlesItemViewHolder(articleBinding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ArticlesItemViewHolder -> holder.bindTo(getItem(position)!!)
            else -> (holder as NetworkStateItemViewHolder).bindView(networkState)
        }
    }

    companion object {
        val TopHeadlinesDiffCallback = object : DiffUtil.ItemCallback<Articles>() {
            override fun areItemsTheSame(
                oldItem: Articles,
                newItem: Articles
            ): Boolean {
                return oldItem.description == newItem.description
            }

            override fun areContentsTheSame(
                oldItem: Articles,
                newItem: Articles
            ): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (hasExtraRow() && position == (itemCount - 1)) {
            TYPE_PROGRESS
        } else {
            TYPE_ITEM
        }
    }

    override fun getPreloadItems(position: Int): MutableList<Articles> {
        val articles = getItem(position)
        return if (TextUtils.isEmpty(articles!!.urlToImage)) {
            emptyList()
        } else singletonList(articles)
    }

    override fun getPreloadRequestBuilder(item: Articles): RequestBuilder<*>? {
        return GlideApp.with(context)
            .load(item.urlToImage)
    }

    inner class ArticlesItemViewHolder(private val binding: ArticlesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(article: Articles) {
            binding.setVariable(BR.article, article)
            fullRequest!!.load(article.urlToImage)
                .thumbnail(thumbRequest!!.load(article.urlToImage))
                .into(binding.ivNewsThumbnail)
            binding.tvArticleTitle.setOnClickListener { view ->
                val transitionName = binding.root.context.getString(R.string.transition_name)
                ViewCompat.setTransitionName(binding.tvArticleTitle, transitionName)
                itemClickCallback.onItemClicked(article, view, transitionName)
            }
        }
    }
}