package com.diraj.mynews.di

import android.app.Application
import androidx.room.Room
import com.diraj.mynews.db.NewsDB
import com.diraj.mynews.db.TopHeadlinesDao
import com.diraj.mynews.network.INewsInterface
import com.diraj.mynews.network.LiveDataCallAdapterFactory
import com.diraj.mynews.util.RateLimiter
import dagger.Module
import dagger.Provides
import io.reactivex.disposables.CompositeDisposable
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module(includes = [ViewModelModule::class])
class AppModule {

    @Singleton
    @Provides
    fun provideNewsApiService(): INewsInterface {
        return Retrofit.Builder()
            .baseUrl("https://newsapi.org")
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .build()
            .create(INewsInterface::class.java)
    }

    @Singleton
    @Provides
    fun provideNewsDB(app: Application): NewsDB {
        return Room.databaseBuilder(app, NewsDB::class.java, "news.db")
            .fallbackToDestructiveMigration()
            .build()
    }

    @Singleton
    @Provides
    fun provideTopHeadlinesDao(db: NewsDB): TopHeadlinesDao {
        return db.topHeadlinesDao()
    }

    @Singleton
    @Provides
    fun provideRateLimiter(): RateLimiter<String> {
        return RateLimiter(10, TimeUnit.MINUTES)
    }

    @Provides
    fun provideCompositeDisposable(): CompositeDisposable {
        return CompositeDisposable()
    }
}