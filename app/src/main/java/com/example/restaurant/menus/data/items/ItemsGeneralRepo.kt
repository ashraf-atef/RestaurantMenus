package com.example.restaurant.menus.data.items

import com.example.restaurant.menus.data.items.local.ItemsLocalRepo
import com.example.restaurant.menus.data.items.remote.ItemsRemoteRepo
import io.reactivex.Maybe
import javax.inject.Inject

class ItemsGeneralRepo @Inject constructor(
    private val itemsLocalRepo: ItemsLocalRepo,
    private val itemsRemoteRepo: ItemsRemoteRepo
) {

    fun getItems(tagName: String): Maybe<List<Item>> =
        itemsLocalRepo.getItems(tagName)
            .flatMap {
                if (it.isEmpty())
                    itemsRemoteRepo.getItems(tagName)
                        .doOnSuccess { remoteDataList ->
                            itemsLocalRepo.insert(remoteDataList).subscribe()
                        }
                        .toMaybe()
                else
                    Maybe.just(it)
            }
}