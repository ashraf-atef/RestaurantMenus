package com.example.restaurant.item

import com.example.restaurant.common.presentationLayer.rx.addTo
import com.example.restaurant.common.presentationLayer.rx.getIoMainTransformer
import com.example.restaurant.common.presentationLayer.view_model.BaseViewModel
import com.example.restaurant.item.data.GettingItemLocalRepo
import javax.inject.Inject

class ItemDetailsViewModel @Inject constructor(private val itemLocalRepo: GettingItemLocalRepo) :
    BaseViewModel<ItemDetailsState>() {

    fun loadFromScratch(itemId: Int) {
        if (!isInitBefore) {
            getItem(itemId)
            isInitBefore = true
        }
    }

    private fun getItem(id: Int) {
        itemLocalRepo.getItem(id)
            .compose(getIoMainTransformer())
            .subscribe(
                { postState(getCurrentState().copy(item = it)) },
                { it.printStackTrace() })
            .addTo(compositeDisposable)
    }

    override fun getInitialState(): ItemDetailsState = ItemDetailsState()
}