package com.example.restaurant.common.dataLayer.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.restaurant.menus.data.items.Item
import com.example.restaurant.menus.data.items.local.ItemsDao
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.local.TagDao


@Database(entities = [Tag::class, Item::class], version = 1)
abstract class LocalDatabase : RoomDatabase() {

    abstract fun tagsDao(): TagDao

    abstract fun itemsDao(): ItemsDao

    companion object {

        lateinit var instance: LocalDatabase
        fun getInstance(context: Context): LocalDatabase {
            synchronized(LocalDatabase::class) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    LocalDatabase::class.java, "database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                return instance
            }
        }
    }
}