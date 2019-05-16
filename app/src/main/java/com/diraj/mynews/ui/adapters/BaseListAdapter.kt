package com.diraj.mynews.ui.adapters

import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.diraj.mynews.network.Resource
import com.diraj.mynews.network.Status

abstract class BaseListAdapter<T, VH : RecyclerView.ViewHolder>(diffCallback: DiffUtil.ItemCallback<T>) :
    PagedListAdapter<T, VH>(diffCallback) {

    protected var networkState: Resource<Any> = Resource(Status.LOADING, null, null)

    protected fun hasExtraRow(): Boolean {
        return networkState.status != null && networkState.status !== Status.SUCCESS
    }

    fun setNewNetworkState(newNetworkState: Resource<Any>) {
        val previousState = this.networkState
        val previousExtraRow = hasExtraRow()
        this.networkState = newNetworkState
        val newExtraRow = hasExtraRow()
        if (previousExtraRow != newExtraRow) {
            if (previousExtraRow) {
                notifyItemRemoved(itemCount)
            } else {
                notifyItemInserted(itemCount)
            }
        } else if (newExtraRow && previousState !== newNetworkState) {
            notifyItemChanged(itemCount - 1)
        }
    }
}