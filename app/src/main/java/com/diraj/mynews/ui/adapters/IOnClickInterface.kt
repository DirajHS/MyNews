package com.diraj.mynews.ui.adapters

import android.view.View

interface IOnClickInterface<T> {

    fun onItemClicked(t: T, view: View)
}