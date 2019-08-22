package com.example.restaurant.common.di.component

import android.app.Application
import com.example.restaurant.common.application.BaseApplication
import com.example.restaurant.common.constants.API_URL_KEY
import com.example.restaurant.common.di.module.*
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjectionModule
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import javax.inject.Named
import javax.inject.Singleton


@Singleton
@Component(
    modules = [AndroidInjectionModule::class,
        ActivityBuilder::class,
        NetModule::class,
        ViewModelModule::class,
        AppModule::class,
        DataModule::class,
        SchedulersModule::class]
)
interface AppComponent : AndroidInjector<DaggerApplication> {

    fun inject(baseApplication: BaseApplication)

    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        @BindsInstance
        @Named(API_URL_KEY)
        fun apiUrl(apiUrl: String): Builder

        fun build(): AppComponent
    }
}