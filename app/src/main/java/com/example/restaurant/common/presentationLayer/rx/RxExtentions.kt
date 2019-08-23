package com.example.restaurant.common.presentationLayer.rx

import com.example.restaurant.common.presentationLayer.rx.schedulers.IoMainSchedulerTransformer
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

fun Disposable.addTo(compositeDisposable: CompositeDisposable) {
    compositeDisposable.add(this)
}

@JvmField
val ioMainTransformer = IoMainSchedulerTransformer<Any>()

@SuppressWarnings("unchecked")
fun <T> getIoMainTransformer(): IoMainSchedulerTransformer<T> = ioMainTransformer as IoMainSchedulerTransformer<T>
