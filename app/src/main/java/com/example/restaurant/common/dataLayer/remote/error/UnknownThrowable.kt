package com.example.restaurant.common.dataLayer.remote.error

class UnknownThrowable : Throwable() {

    override fun toString(): String {
        return "Unknown Error"
    }
}
