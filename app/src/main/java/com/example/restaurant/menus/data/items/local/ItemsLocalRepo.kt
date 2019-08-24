package com.example.restaurant.menus.data.items.local

import com.example.restaurant.menus.data.items.Item
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class ItemsLocalRepo @Inject constructor(private val itemsDao: ItemsDao) {

    fun insert(items: List<Item>) : Completable = itemsDao.insert(items)

    fun getItems(tagName: String) : Single<List<Item>> = itemsDao.getItems(tagName)
}