package com.diraj.mynews.ui

import android.view.View
import com.diraj.mynews.model.Articles

interface IOnNewsClickInterface {
    fun onNewsArticleClick(article: Articles, view: View, transitionName: String)
}