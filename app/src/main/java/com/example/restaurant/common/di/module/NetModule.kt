package com.example.restaurant.common.di.module

import com.example.restaurant.common.constants.API_URL_KEY
import com.example.restaurant.menus.data.tags.remote.TagsApi
import com.google.gson.FieldNamingPolicy
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named


@Module
abstract class NetModule {

    @Binds
    @Named(API_URL_KEY)
    abstract fun provideApiUrl(apiUrl: String): String

    @Module
    companion object {

        @Provides
        @Reusable
        @JvmStatic
        fun providesOkHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .build()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesGson(): Gson {
            val gsonBuilder = GsonBuilder()
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.IDENTITY)
            return gsonBuilder.create()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesRetrofit(
            @Named(API_URL_KEY) apiUrl: String, gson: Gson, okHttpClient: OkHttpClient
        ): Retrofit {
            return Retrofit.Builder()
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(okHttpClient)
                .baseUrl(apiUrl)
                .build()
        }

        @Provides
        @Reusable
        @JvmStatic
        fun providesProductsApi(retrofit: Retrofit): TagsApi {
            return retrofit.create(TagsApi::class.java)
        }
    }
}