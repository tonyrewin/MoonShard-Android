package io.moonshard.moonshard.di.modules

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import io.moonshard.moonshard.common.TopicStorage
import javax.inject.Singleton


@Module
class ApplicationModule(var context: Context) {
    @Provides
    @Singleton
    fun providesContext(): Context {
        return context
    }

    @Provides
    @Singleton
    fun provideGson(): Gson {
        return Gson()
    }

    @Provides
    @Singleton
    fun provideTopicStorage(context: Context): TopicStorage {
        return TopicStorage(context)
    }
}