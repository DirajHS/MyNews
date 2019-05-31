package com.diraj.mynews.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.library.baseAdapters.BR
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diraj.mynews.databinding.ArticlesItemLayoutBinding
import com.diraj.mynews.databinding.NetworkItemBinding
import com.diraj.mynews.model.Articles

class TopHeadlinesAdapter(
    private val itemClickCallback: IOnClickInterface<Articles>,
    private val retry: () -> Unit
) : BaseListAdapter<Articles, RecyclerView.ViewHolder>(TopHeadlinesDiffCallback) {

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
            Log.d("diraj", "loading view type")
            TYPE_PROGRESS
        } else {
            Log.d("diraj", "loaded")
            TYPE_ITEM
        }
    }

    inner class ArticlesItemViewHolder(private val binding: ArticlesItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindTo(article: Articles) {
            binding.setVariable(BR.article, article)
            binding.root.setOnClickListener { view ->
                itemClickCallback.onItemClicked(article, view)
            }
        }
    }
}