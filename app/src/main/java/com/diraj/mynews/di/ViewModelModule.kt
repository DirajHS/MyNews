package com.diraj.mynews.di

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.diraj.mynews.ui.TopHeadlinesViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(TopHeadlinesViewModel::class)
    abstract fun bindTopHeadlinesViewModel(topHeadlinesViewModel: TopHeadlinesViewModel): ViewModel

    @Binds
    abstract fun bindViewModelFactory(viewModelFactory: NewsViewModelFactory): ViewModelProvider.Factory
}