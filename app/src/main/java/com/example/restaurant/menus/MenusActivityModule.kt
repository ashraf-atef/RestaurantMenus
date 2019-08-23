package com.example.restaurant.menus

import dagger.Binds
import dagger.Module

@Module
abstract class MenusActivityModule {

    @Binds
    abstract fun provideTagsAdapterItemClickListener(menusActivity: MenusActivity): TagsAdapter.ItemClickListener

    @Binds
    abstract fun provideItemsAdapterItemClickListener(menusActivity: MenusActivity): ItemsAdapter.ItemClickListener
}