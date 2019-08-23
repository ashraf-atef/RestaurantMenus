package com.example.restaurant.menus.data.items

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "item")
data class Item(
    @PrimaryKey
    val id: Int,
    val name: String,
    val tagName: String,
    val photoUrl: String,
    val description: String
)