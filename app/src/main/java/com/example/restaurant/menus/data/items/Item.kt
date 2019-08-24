package com.example.restaurant.menus.data.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(
    tableName = "item",
    indices = [Index(value = ["itemId", "tagName"], unique = true)]
)
data class Item(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @Transient
    val id: Int,
    @SerializedName("id")
    val itemId: Int,
    val tagName: String,
    val name: String,
    val photoUrl: String,
    val description: String
)