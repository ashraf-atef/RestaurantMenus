package com.example.restaurant.menus.data.items

import com.example.restaurant.menus.data.items.local.ItemsLocalRepo
import com.example.restaurant.menus.data.items.remote.ItemsRemoteRepo
import com.example.restaurant.menus.data.errors.NoMoreOfflineDataThrowable
import io.reactivex.Single
import javax.inject.Inject

class ItemsGeneralRepo @Inject constructor(
    private val itemsLocalRepo: ItemsLocalRepo,
    private val itemsRemoteRepo: ItemsRemoteRepo
) {

    fun getItems(tagName: String): Single<List<Item>> =
        itemsRemoteRepo.getItems(tagName)
            .flatMap {
                itemsLocalRepo.insert(it).andThen(
                    /**
                     * Fetch the same list from the database again to get a new list with auto incremented ids
                     * because we can't depend on item id returned from api as primary key because it's not
                     * unique.
                     */
                    itemsLocalRepo.getItems(tagName)
                )

            }
            .onErrorResumeNext {
                itemsLocalRepo.getItems(tagName)
                    .doOnSuccess {
                        if (it.isEmpty()) throw NoMoreOfflineDataThrowable()
                    }
            }
}