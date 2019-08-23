package com.example.restaurant.common.di.module

import com.example.restaurant.common.presentationLayer.rx.schedulers.SchedulersService
import com.example.restaurant.common.presentationLayer.rx.schedulers.SchedulersServiceImpl
import dagger.Module
import dagger.Provides

@Module
class SchedulersModule {

    @Provides
    fun provideSchedulersService(): SchedulersService = SchedulersServiceImpl()
}