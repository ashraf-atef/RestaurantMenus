package com.example.restaurant.common.presentationLayer.rx.errors

import io.reactivex.exceptions.CompositeException
import io.reactivex.functions.Consumer

abstract class CompositeErrorConsumer: Consumer<Throwable> {

    override fun accept(t: Throwable) = handle(if (t is CompositeException) t.exceptions.last() else t)

    abstract fun handle(t: Throwable)
}