package com.example.restaurant.common.presentationLayer.view_model

import androidx.annotation.CallSuper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

/**
 * Base view model of generic state
 */
abstract class BaseViewModel<T> : ViewModel() {

    /**
     * Mutable live data that holds the state of view model
     */
    val liveData: MutableLiveData<T> = MutableLiveData()
    /**
     * Composite disposable to add all disposable on it to be cleared in onClear()
     */
    protected val compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun isInitBefore(): Boolean = liveData.value != null

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