package com.example.restaurant.common.presentationLayer.view_model

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class BaseViewModel<T> : ViewModel() {

    val liveData: MutableLiveData<T> = MutableLiveData()
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()
    protected var isInitBefore: Boolean = false

    abstract fun getInitialState(): T

    fun postState(state: T) {
        liveData.postValue(state)
    }

    fun getCurrentState(): T = liveData.value ?: getInitialState()

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}