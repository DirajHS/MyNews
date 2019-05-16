package com.diraj.mynews.di

import android.app.Application
import com.diraj.mynews.NewsApplication
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(
    modules = [AndroidSupportInjectionModule::class,
        AppModule::class,
        MainActivityModule::class]
)
interface NewsAppComponent : AndroidInjector<NewsApplication> {

    override
    fun inject(instance: NewsApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): NewsAppComponent
    }
}