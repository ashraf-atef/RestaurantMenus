package com.example.restaurant.menus.data.items.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restaurant.menus.data.items.Item
import io.reactivex.Completable
import io.reactivex.Maybe

@Dao
interface ItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Item>): Completable

    @Query("Select * from item where tagName = :tagName")
    fun getItems(tagName: String): Maybe<List<Item>>
}