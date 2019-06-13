package com.diraj.mynews.ui.adapters

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import timber.log.Timber

object BindingAdapters {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, url: String?) {
        Timber.d(url+":"+imageView.height)
        Glide.with(imageView.context)
            .load(url)
            .into(imageView)
    }
}