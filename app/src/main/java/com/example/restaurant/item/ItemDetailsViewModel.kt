package com.example.restaurant.item

import com.example.restaurant.common.presentationLayer.BaseViewModel
import com.example.restaurant.common.presentationLayer.addTo
import com.example.restaurant.item.data.GettingItemLocalRepo
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
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
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                { postState(getCurrentState().copy(item = it)) },
                { it.printStackTrace() })
            .addTo(compositeDisposable)
    }

    override fun getInitialState(): ItemDetailsState = ItemDetailsState()
}