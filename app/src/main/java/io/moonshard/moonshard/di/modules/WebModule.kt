package io.moonshard.moonshard.di.modules

import android.content.Context
import dagger.Module
import dagger.Provides
import io.moonshard.moonshard.API
import io.moonshard.moonshard.common.ApiConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class WebModule(var context: Context) {

    @Provides
    @Singleton
    fun providesRetrofit(): Retrofit {

        val client = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor())
            .connectTimeout(1, TimeUnit.MINUTES)
            .build()

        return Retrofit.Builder()
            .baseUrl(ApiConstants.COMPLEX_BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun providesMovieApi(retrofit: Retrofit): API {
        return retrofit.create(API::class.java)
    }
}