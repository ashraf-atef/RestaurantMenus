package com.example.restaurant.common.presentationLayer.schedulers

import io.reactivex.Scheduler

interface SchedulersService {
    fun io(): Scheduler

    fun mainThread(): Scheduler
}
