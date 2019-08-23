package com.example.restaurant.menus

import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.common.presentationLayer.rx.addTo
import com.example.restaurant.common.presentationLayer.view_model.BaseViewModel
import com.example.restaurant.common.presentationLayer.rx.errors.CompositeErrorConsumer
import com.example.restaurant.common.presentationLayer.rx.getIoMainTransformer
import com.example.restaurant.menus.data.items.ItemsGeneralRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import io.reactivex.functions.Consumer
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
            .compose(getIoMainTransformer())
            .subscribe(Consumer {
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
            }, object : CompositeErrorConsumer() {
                override fun handle(t: Throwable) {
                    postState(
                        getCurrentState().copy(
                            tagsLoading = null,
                            error = when (t) {
                                is ConnectionThrowable -> Errors.NO_MORE_OFFLINE_DATA
                                is NoDataAvailableThrowable -> Errors.NO_DATA_AVAILABLE
                                else -> Errors.UNKNOWN
                            }
                        )
                    )
                }
            }
            ).addTo(compositeDisposable)
    }

    //TODO: dispose the previous request if found before starting new one
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
            .compose(getIoMainTransformer())
            .subscribe(Consumer {
                postState(
                    getCurrentState().copy(itemsLoading = false, items = it)
                )
            }, object : CompositeErrorConsumer() {
                override fun handle(t: Throwable) {
                    postState(
                        getCurrentState().copy(
                            itemsLoading = false,
                            itemErrors = when (t) {
                                is ConnectionThrowable -> Errors.NO_MORE_OFFLINE_DATA
                                is NoDataAvailableThrowable -> Errors.NO_DATA_AVAILABLE
                                else -> Errors.UNKNOWN
                            }
                        )
                    )
                }
            }
            )
            .addTo(compositeDisposable)
    }

    fun retryLoadItems() {
        lastSelectedTag?.let {
            getItems(it)
        }
    }

    override fun getInitialState(): MenusState = MenusState(
        tagsLoading = TagsLoading.LOAD_FROM_SCRATCH
    )

}