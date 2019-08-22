package com.example.restaurant.common.di.module

import com.example.restaurant.menus.MenusActivity
import com.example.restaurant.menus.MenusActivityModule
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class ActivityBuilder {
    @ContributesAndroidInjector(modules = [MenusActivityModule::class])
    abstract fun bindDataActivity(): MenusActivity
}
