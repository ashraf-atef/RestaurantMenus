package com.example.restaurant

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import com.example.restaurant.common.dataLayer.local.LocalDatabase

class InMemoryLocalDatabase {

    val localDatabase: LocalDatabase by lazy {
        Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            LocalDatabase::class.java
        ).build()
    }
}