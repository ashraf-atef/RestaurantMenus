package com.example.restaurant.menus

import androidx.lifecycle.Observer
import com.example.restaurant.BaseTest
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.errors.NoMoreOfflineDataThrowable
import io.reactivex.Maybe
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*


class DataViewModelTest : BaseTest() {

    lateinit var menusViewModel: MenusViewModel
    @Mock
    lateinit var dataGeneralRepo: TagsGeneralRepo
    @Mock
    lateinit var observer: Observer<MenusState>
    private val tagList: List<Tag> by lazy {
        listOf<Tag>()
    }

    override fun setup() {
        super.setup()
        `when`(dataGeneralRepo.getData()).then { Maybe.just(tagList) }
        menusViewModel = MenusViewModel(dataGeneralRepo)
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
       `when`(dataGeneralRepo.getData()).then { Maybe.error<Any>(NoMoreOfflineDataThrowable()) }

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
        `when`(dataGeneralRepo.getData()).then { Maybe.error<Any>(NoDataAvailableThrowable()) }
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