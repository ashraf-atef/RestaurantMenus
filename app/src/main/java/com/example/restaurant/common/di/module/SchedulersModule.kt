package com.example.restaurant.common.di.module

import com.example.restaurant.common.presentationLayer.schedulers.SchedulersService
import com.example.restaurant.common.presentationLayer.schedulers.SchedulersServiceImpl
import dagger.Module
import dagger.Provides

@Module
class SchedulersModule {

    @Provides
    fun provideSchedulersService(): SchedulersService = SchedulersServiceImpl()
}