package com.example.restaurant.menus.data.items.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restaurant.menus.data.items.Item
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface ItemsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(items: List<Item>): Completable

    @Query("Select * from item where tagName = :tagName")
    fun getItems(tagName: String): Maybe<List<Item>>

    @Query("Select * from item where id_ = :id")
    fun getItem(id: Int): Single<Item>
}