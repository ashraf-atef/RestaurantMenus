package com.example.restaurant.menus

import com.example.restaurant.common.presentationLayer.BaseViewModel
import com.example.restaurant.common.presentationLayer.addTo
import com.example.restaurant.menus.data.tags.DataGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.errors.NoMoreOfflineDataThrowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MenusViewModel @Inject constructor(private val dataGeneralRepo: DataGeneralRepo) : BaseViewModel<MenusState>() {

    fun loadFromScratch() {
        if (!isInitBefore) {
            loadInitial()
            isInitBefore = true
        }
    }

    fun refresh() {
        dataGeneralRepo.loadFromScratch()
        loadInitial()
    }

    private fun loadInitial() {
        postState(getInitialState())
        loadData()
    }

    fun loadMore() {
        postState(
            getCurrentState().copy(
                loading = DataLoading.LOAD_MORE,
                error = null
            )
        )
        loadData()
    }

    private fun loadData() {
        dataGeneralRepo.getData()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val newDataList = getCurrentState().tagList.toMutableList()
                    newDataList.addAll(it)
                    postState(
                        getCurrentState().copy(
                            tagList = newDataList,
                            loading = null,
                            error = null
                        )
                    )
                },
                {
                    val throwable = if (it is CompositeException)
                        it.exceptions.last()
                    else
                        it

                    postState(
                        getCurrentState().copy(
                            loading = null,
                            error = when (throwable) {
                                is NoMoreOfflineDataThrowable -> DataErrors.NO_MORE_OFFLINE_DATA
                                is NoDataAvailableThrowable -> DataErrors.NO_DATA_AVAILABLE
                                else -> DataErrors.UNKNOWN
                            }
                        )
                    )
                }
            ).addTo(compositeDisposable)
    }

    override fun getInitialState(): MenusState {
        return MenusState(loading = DataLoading.LOAD_FROM_SCRATCH)
    }
}