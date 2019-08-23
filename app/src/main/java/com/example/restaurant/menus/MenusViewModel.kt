package com.example.restaurant.menus

import androidx.annotation.VisibleForTesting
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.common.presentationLayer.rx.addTo
import com.example.restaurant.common.presentationLayer.rx.disposeIfNot
import com.example.restaurant.common.presentationLayer.view_model.BaseViewModel
import com.example.restaurant.common.presentationLayer.rx.errors.CompositeErrorConsumer
import com.example.restaurant.common.presentationLayer.rx.getIoMainTransformer
import com.example.restaurant.menus.data.items.ItemsGeneralRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Consumer
import javax.inject.Inject

class MenusViewModel @Inject constructor(
    private val tagsGeneralRepo: TagsGeneralRepo,
    private val itemsGeneralRepo: ItemsGeneralRepo
) : BaseViewModel<MenusState>() {

    @VisibleForTesting
    var lastSelectedTag: Tag? = null
    private var itemsDisposable: Disposable? = null

    fun loadFromScratch() {
        if (!isInitBefore) {
            loadInitial()
            isInitBefore = true
        }
    }

    fun refresh() {
        tagsGeneralRepo.rest()
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
                tagsError = null
            )
        )
        loadTags()
    }

    private fun loadTags() {
        tagsGeneralRepo.getTags()
            .compose(getIoMainTransformer())
            .subscribe(Consumer {
                val newDataList = getCurrentState().tags.toMutableList()
                newDataList.addAll(it)
                postState(
                    getCurrentState().copy(
                        tags = newDataList,
                        tagsLoading = null,
                        tagsError = null,
                        itemsInitialState = (lastSelectedTag == null)
                    )
                )
            }, object : CompositeErrorConsumer() {
                override fun handle(t: Throwable) {
                    postState(
                        getCurrentState().copy(
                            tagsLoading = null,
                            tagsError = when (t) {
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

    fun getItems(tag: Tag) {
        itemsDisposable?.disposeIfNot()
        lastSelectedTag = tag

        postState(
            getCurrentState().copy(
                itemErrors = null,
                itemsLoading = true,
                itemsInitialState = false,
                items = listOf()
            )
        )

        itemsDisposable= itemsGeneralRepo.getItems(tag.tagName)
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
    }

    fun retryLoadItems() {
        lastSelectedTag?.let {
            getItems(it)
        }
    }

    override fun getInitialState(): MenusState = MenusState(
        tagsLoading = TagsLoading.LOAD_FROM_SCRATCH
    )

    override fun onCleared() {
        super.onCleared()
        itemsDisposable?.disposeIfNot()
    }
}