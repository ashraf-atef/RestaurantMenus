package com.example.restaurant.menus

import dagger.Binds
import dagger.Module

@Module
abstract class MenusActivityModule {

    @Binds
    abstract fun provideDataAdapterItemClickListener(menusActivity: MenusActivity): TagsAdapter.ItemClickListener
}