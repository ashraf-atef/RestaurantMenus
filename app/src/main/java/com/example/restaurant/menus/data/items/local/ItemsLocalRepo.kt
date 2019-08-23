package com.example.restaurant.menus.data.items.local

import com.example.restaurant.menus.data.items.Item
import io.reactivex.Completable
import io.reactivex.Maybe
import javax.inject.Inject

class ItemsLocalRepo @Inject constructor(private val itemsDao: ItemsDao) {

    fun insert(items: List<Item>) : Completable = itemsDao.insert(items)

    fun getItems(tagName: String) : Maybe<List<Item>> = itemsDao.getItems(tagName)
}