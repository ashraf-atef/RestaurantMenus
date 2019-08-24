package com.example.restaurant.menus.data.tags.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.restaurant.menus.data.tags.Tag
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single

@Dao
interface TagDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(posts : List<Tag>): Completable

    @Query("SELECT * FROM Tag LIMIT :index, :limit")
    fun getTags(index: Int, limit: Int) : Single<List<Tag>>
}