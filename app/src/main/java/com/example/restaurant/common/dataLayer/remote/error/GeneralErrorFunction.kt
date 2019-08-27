package com.example.restaurant.common.dataLayer.remote.error

import io.reactivex.functions.Function
import java.io.IOException

class GeneralErrorFunction<T> : Function<Throwable, T> {
    override fun apply(t: Throwable): T {
        if (t is IOException)
            throw ConnectionThrowable()
        throw t
    }
}