package com.diraj.mynews.ui

import com.diraj.mynews.model.Articles

interface IOnNewsClickInterface {
    fun onNewsArticleClick(article: Articles)
}