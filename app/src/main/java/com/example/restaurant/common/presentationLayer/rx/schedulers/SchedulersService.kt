package com.example.restaurant.common.presentationLayer.rx.schedulers

import io.reactivex.Scheduler

interface SchedulersService {
    fun io(): Scheduler

    fun mainThread(): Scheduler
}
