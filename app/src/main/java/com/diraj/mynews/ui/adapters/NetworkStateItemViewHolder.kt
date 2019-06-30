package com.diraj.mynews.ui.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.diraj.mynews.network.Resource
import com.diraj.mynews.network.Status

class NetworkStateItemViewHolder(
    private val binding: com.diraj.mynews.databinding.NetworkItemBinding,
    retry: (() -> Unit?)?
) :
    RecyclerView.ViewHolder(binding.root) {

    init {
        binding.tvError.setOnClickListener { retry?.invoke() }
    }

    fun bindView(networkState: Resource<Any>?) {

        if (networkState != null && networkState.status === Status.LOADING) {
            binding.pbProgress.visibility = View.VISIBLE
        } else {
            binding.pbProgress.visibility = View.GONE
        }
        if (networkState != null && networkState.status === Status.ERROR) {
            binding.tvError.visibility = View.VISIBLE
            binding.ivOops.visibility = View.VISIBLE
        } else {
            binding.tvError.visibility = View.GONE
            binding.ivOops.visibility = View.GONE
        }
    }
}