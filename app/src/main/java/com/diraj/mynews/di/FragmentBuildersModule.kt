package com.diraj.mynews.di

import com.diraj.mynews.ui.TopHeadlinesFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentBuildersModule {
    @ContributesAndroidInjector
    abstract fun contributeTopHeadlinesFragment(): TopHeadlinesFragment
}