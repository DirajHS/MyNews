package com.diraj.mynews.ui

import androidx.appcompat.widget.Toolbar
import com.google.android.material.appbar.CollapsingToolbarLayout

interface IActionBarInterface {

    fun setUpActionBar()

    fun setUpToolbar(collapsingToolbarLayout: CollapsingToolbarLayout,
                     toolbar: Toolbar)
}