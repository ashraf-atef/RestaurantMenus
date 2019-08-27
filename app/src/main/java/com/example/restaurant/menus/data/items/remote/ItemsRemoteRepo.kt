package com.example.restaurant.menus.data.items.remote

import com.example.restaurant.menus.data.items.Item
import io.reactivex.Single
import javax.inject.Inject

class ItemsRemoteRepo @Inject constructor(private val itemsApi: ItemsApi) {

    fun getItems(tagName: String): Single<List<Item>> =
        itemsApi.getItems(tagName)
            .map {
                it.items.map { item -> item.copy(tagName = tagName)}
            }
}