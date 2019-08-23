package com.example.restaurant.item.data

import com.example.restaurant.menus.data.items.Item
import com.example.restaurant.menus.data.items.local.ItemsDao
import io.reactivex.Single
import javax.inject.Inject

class GettingItemLocalRepo @Inject constructor(private val itemsDao: ItemsDao) {

    fun getItem(Id: Int): Single<Item> = itemsDao.getItem(Id)
}
