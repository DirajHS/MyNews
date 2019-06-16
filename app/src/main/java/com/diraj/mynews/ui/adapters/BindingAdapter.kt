package com.diraj.mynews.ui.adapters

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.diraj.mynews.di.GlideApp
import com.diraj.mynews.di.GlideRequest

object BindingAdapter {

    @JvmStatic
    @BindingAdapter("imageUrl")
    fun loadImage(imageView: ImageView, url: String?) {

        val fullRequest: GlideRequest<Drawable>?
        val thumbRequest: GlideRequest<Drawable>?
        val glideRequests = GlideApp.with(imageView.context)
        fullRequest = glideRequests
            .asDrawable()
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .fitCenter()
            .placeholder(ColorDrawable(Color.GRAY))

        thumbRequest = glideRequests
            .asDrawable()
            .override(250, 250)
            .transition(DrawableTransitionOptions.withCrossFade())

        fullRequest.load(url)
            .thumbnail(thumbRequest.load(url))
            .into(imageView)
    }
}