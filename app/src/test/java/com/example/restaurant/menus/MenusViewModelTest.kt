package com.example.restaurant.menus

import androidx.lifecycle.Observer
import com.example.restaurant.BaseTest
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.menus.data.items.ItemsGeneralRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import io.reactivex.Maybe
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*


class MenusViewModelTest : BaseTest() {

    lateinit var menusViewModel: MenusViewModel
    @Mock
    lateinit var tagsGeneralRepo: TagsGeneralRepo
    @Mock
    lateinit var itemsGeneralRepo: ItemsGeneralRepo
    @Mock
    lateinit var observer: Observer<MenusState>
    private val tagList: List<Tag> by lazy {
        listOf<Tag>()
    }

    override fun setup() {
        super.setup()
        `when`(tagsGeneralRepo.getData()).then { Maybe.just(tagList) }
        menusViewModel = MenusViewModel(tagsGeneralRepo, itemsGeneralRepo)
        menusViewModel.liveData.observeForever(observer)
    }

    @Test
    fun `load from scratch WHEN load data return a list EXPECT pushing initial state then data state`() {
        menusViewModel.loadFromScratch()

        println("verify")
        verify(observer).onChanged(menusViewModel.getInitialState())
        verify(observer).onChanged(
            menusViewModel.getCurrentState().copy(
                tags = tagList,
                tagsLoading = null,
                error = null
            )
        )
    }

    @Test
    fun `refresh WHEN load data return a list EXPECT pushing initial state then data state`() {
        menusViewModel.refresh()
        verify(observer).onChanged(menusViewModel.getInitialState())
        verify(observer).onChanged(
            menusViewModel.getCurrentState().copy(
                tags = tagList,
                tagsLoading = null,
                error = null
            )
        )
    }

    @Test
    fun `load more WHEN load data return a list EXPECT pushing initial state then data state`() {
        menusViewModel.loadMoreTags()
        verify(observer).onChanged( menusViewModel.getCurrentState().copy(
            tagsLoading = TagsLoading.LOAD_MORE,
            error = null
        ))
        verify(observer).onChanged(
            menusViewModel.getCurrentState().copy(
                tags = tagList,
                tagsLoading = null,
                error = null
            )
        )
    }

    @Test
    fun `load more WHEN load data return a no more data offline EXPECT pushing initial state then data state`() {
       `when`(tagsGeneralRepo.getData()).then { Maybe.error<Any>(ConnectionThrowable()) }

        menusViewModel.loadMoreTags()
        verify(observer).onChanged( menusViewModel.getCurrentState().copy(
            tagsLoading = TagsLoading.LOAD_MORE,
            error = null
        ))
        verify(observer).onChanged(
            menusViewModel.getCurrentState().copy(
                tagsLoading = null,
                error = Errors.NO_MORE_OFFLINE_DATA
            )
        )
    }

    @Test
    fun `load more WHEN load data return a no data available EXPECT pushing initial state then data state`() {
        `when`(tagsGeneralRepo.getData()).then { Maybe.error<Any>(NoDataAvailableThrowable()) }
        menusViewModel.loadMoreTags()
        verify(observer).onChanged( menusViewModel.getCurrentState().copy(
            tagsLoading = TagsLoading.LOAD_MORE,
            error = null
        ))
        verify(observer).onChanged(
            menusViewModel.getCurrentState().copy(
                tagsLoading = null,
                error = Errors.NO_DATA_AVAILABLE
            )
        )
    }
}