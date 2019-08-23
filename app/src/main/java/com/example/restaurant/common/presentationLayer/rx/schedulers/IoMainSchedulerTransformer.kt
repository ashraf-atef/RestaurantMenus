package com.example.restaurant.common.presentationLayer.rx.schedulers

import io.reactivex.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher

/**
 * Transformer that apply subscribe on IO and observe on main tread
 * @param <T>
</T> */
class IoMainSchedulerTransformer<T> : ObservableTransformer<T, T>, FlowableTransformer<T, T>, SingleTransformer<T, T>,
    MaybeTransformer<T, T>, CompletableTransformer {

    private val subscriptionScheduler: Scheduler
        get() = Schedulers.io()

    private val observationScheduler: Scheduler
        get() = AndroidSchedulers.mainThread()

    override fun apply(upstream: Observable<T>): ObservableSource<T> {
        return upstream
            .subscribeOn(subscriptionScheduler)
            .observeOn(observationScheduler)
    }

    override fun apply(upstream: Completable): CompletableSource {
        return upstream
            .subscribeOn(subscriptionScheduler)
            .observeOn(observationScheduler)
    }

    override fun apply(upstream: Flowable<T>): Publisher<T> {
        return upstream
            .subscribeOn(subscriptionScheduler)
            .observeOn(observationScheduler)
    }

    override fun apply(upstream: Maybe<T>): MaybeSource<T> {
        return upstream
            .subscribeOn(subscriptionScheduler)
            .observeOn(observationScheduler)
    }

    override fun apply(upstream: Single<T>): SingleSource<T> {
        return upstream
            .subscribeOn(subscriptionScheduler)
            .observeOn(observationScheduler)
    }
}