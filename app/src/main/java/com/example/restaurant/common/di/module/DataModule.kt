package com.example.restaurant.common.di.module

import android.content.Context
import com.example.restaurant.menus.data.tags.local.TagDao
import com.example.restaurant.common.dataLayer.local.LocalDatabase
import com.example.restaurant.menus.data.items.local.ItemsDao
import dagger.Module
import dagger.Provides
import dagger.Reusable

@Module
class DataModule {

    @Provides
    @Reusable
    fun provideProductsDatabase(context: Context): LocalDatabase = LocalDatabase.getInstance(context)

    @Provides
    fun provideTagsDAO(localDatabase: LocalDatabase): TagDao = localDatabase.tagsDao()

    @Provides
    fun provideItemsDAO(localDatabase: LocalDatabase): ItemsDao = localDatabase.itemsDao()
}