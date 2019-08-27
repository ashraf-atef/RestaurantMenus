package com.example.restaurant.menus.data.tags

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Tag")
data class Tag(
    @NonNull
    @PrimaryKey
    val tagName: String,
    val photoURL: String,
    @Transient
    @ColumnInfo(name = "pageNumber")
    val pageNumber: Int
)