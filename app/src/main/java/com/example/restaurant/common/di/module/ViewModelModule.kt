package com.example.restaurant.common.di.module

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.restaurant.menus.MenusViewModel
import com.example.restaurant.common.presentationLayer.view_model.ViewModelFactory
import com.example.restaurant.common.presentationLayer.view_model.ViewModelKey
import com.example.restaurant.item.ItemDetailsViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(MenusViewModel::class)
    abstract fun bindDataViewModel(menusViewModel: MenusViewModel): ViewModel

    @Binds
    @IntoMap
    @ViewModelKey(ItemDetailsViewModel::class)
    abstract fun bindItemDetailsViewModel(itemDetailsViewModel: ItemDetailsViewModel): ViewModel

    @Binds
    abstract fun provideViewModelFactory(viewModelFactory: ViewModelFactory): ViewModelProvider.Factory
}