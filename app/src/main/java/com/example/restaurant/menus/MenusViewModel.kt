package com.example.restaurant.menus

import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.common.presentationLayer.BaseViewModel
import com.example.restaurant.common.presentationLayer.addTo
import com.example.restaurant.menus.data.items.ItemsGeneralRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.errors.NoMoreOfflineDataThrowable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.exceptions.CompositeException
import io.reactivex.schedulers.Schedulers
import javax.inject.Inject

class MenusViewModel @Inject constructor(
    private val tagsGeneralRepo: TagsGeneralRepo,
    private val itemsGeneralRepo: ItemsGeneralRepo
) : BaseViewModel<MenusState>() {

    var lastSelectedTag: Tag? = null

    fun loadFromScratch() {
        if (!isInitBefore) {
            loadInitial()
            isInitBefore = true
        }
    }

    fun refresh() {
        tagsGeneralRepo.loadFromScratch()
        loadInitial()
    }

    private fun loadInitial() {
        lastSelectedTag = null
        postState(getInitialState())
        loadTags()
    }

    fun loadMoreTags() {
        postState(
            getCurrentState().copy(
                tagsLoading = TagsLoading.LOAD_MORE,
                error = null
            )
        )
        loadTags()
    }

    private fun loadTags() {
        tagsGeneralRepo.getData()
            //TODO: Make IO Transformer and use it
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    val newDataList = getCurrentState().tags.toMutableList()
                    newDataList.addAll(it)
                    postState(
                        getCurrentState().copy(
                            tags = newDataList,
                            tagsLoading = null,
                            error = null,
                            itemsInitialState = (lastSelectedTag == null)
                        )
                    )
                },
                {
                    //Make consumer to composite exception
                    val throwable = if (it is CompositeException)
                        it.exceptions.last()
                    else
                        it

                    postState(
                        getCurrentState().copy(
                            tagsLoading = null,
                            error = when (throwable) {
                                //TODO: Replace this with connection throwable
                                is NoMoreOfflineDataThrowable -> Errors.NO_MORE_OFFLINE_DATA
                                is NoDataAvailableThrowable -> Errors.NO_DATA_AVAILABLE
                                else -> Errors.UNKNOWN
                            }
                        )
                    )
                }
            ).addTo(compositeDisposable)
    }

    fun getItems(tag: Tag) {
        lastSelectedTag = tag

        postState(
            getCurrentState().copy(
                itemErrors = null,
                itemsLoading = true,
                itemsInitialState = false,
                items = listOf()
            )
        )

        itemsGeneralRepo.getItems(tag.tagName)
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(
                {
                    postState(
                        if (it.isEmpty())
                            getCurrentState().copy(itemsLoading = false, itemErrors = Errors.NO_DATA_AVAILABLE)
                        else
                            getCurrentState().copy(itemsLoading = false, items = it)
                    )
                },
                {
                    val throwable = if (it is CompositeException)
                        it.exceptions.last()
                    else
                        it

                    postState(
                        if (throwable is ConnectionThrowable)
                            getCurrentState().copy(itemsLoading = false, itemErrors = Errors.NO_MORE_OFFLINE_DATA)
                        else
                            getCurrentState().copy(itemsLoading = false, itemErrors = Errors.UNKNOWN)
                    )
                }
            )
            .addTo(compositeDisposable)
    }

    fun retryLoadItems() {
        lastSelectedTag?.let {
            getItems(it)
        }
    }

    override fun getInitialState(): MenusState {
        return MenusState(
            tagsLoading = TagsLoading.LOAD_FROM_SCRATCH
        )
    }
}