package com.example.restaurant.menus

import androidx.lifecycle.Observer
import com.example.restaurant.BaseTest
import com.example.restaurant.menus.data.items.Item
import com.example.restaurant.menus.data.items.ItemsGeneralRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.tags.TagsGeneralRepo
import com.example.restaurant.menus.data.errors.NoMoreOfflineDataThrowable
import io.reactivex.Single
import org.junit.Test
import org.mockito.ArgumentMatchers
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
    private val itemsList: List<Item> by lazy {
        listOf<Item>()
    }

    override fun setup() {
        super.setup()
        menusViewModel = MenusViewModel(tagsGeneralRepo, itemsGeneralRepo)
        menusViewModel.liveData.observeForever(observer)
    }

    @Test
    fun `load from scratch WHEN load tags return a list and selected tag is null EXPECT pushing initial state then tags state`() {
        mockTagsReposReturnValidData()
        menusViewModel.loadFromScratch()

        verify(observer).onChanged(menusViewModel.getInitialState())
        verify(observer).onChanged(
            MenusState(
                tags = tagList,
                itemsInitialState = true
            )
        )
    }

    @Test
    fun `refresh WHEN load tags return a list and selected tag is null EXPECT pushing initial state then tags state`() {
        mockTagsReposReturnValidData()

        menusViewModel.refresh()
        verify(observer).onChanged(menusViewModel.getInitialState())
        verify(observer).onChanged(
            MenusState(
                tags = tagList,
                itemsInitialState = true
            )
        )
    }

    @Test
    fun `load more WHEN load tags return a list and selected tag is null EXPECT pushing load more then data state`() {
        mockTagsReposReturnValidData()

        menusViewModel.loadMoreTags()

        verify(observer).onChanged(
            MenusState(
                tagsLoading = TagsLoading.LOAD_MORE
            )
        )
        verify(observer).onChanged(
            MenusState(
                tags = tagList,
                itemsInitialState = true
            )
        )
    }

    @Test
    fun `load more WHEN load tags return a list and selected tag is not null EXPECT pushing load more then data state`() {
        mockTagsReposReturnValidData()
        menusViewModel.lastSelectedTag = mock(Tag::class.java)

        menusViewModel.loadMoreTags()

        verify(observer).onChanged(
            MenusState(
                tagsLoading = TagsLoading.LOAD_MORE
            )
        )
        verify(observer).onChanged(
            MenusState(
                tags = tagList
            )
        )
    }

    @Test
    fun `load more WHEN load tags return a no more offline throwable EXPECT pushing load more then no more offline data state`() {
        `when`(tagsGeneralRepo.getTags()).then { Single.error<Any>(NoMoreOfflineDataThrowable()) }

        menusViewModel.loadMoreTags()

        verify(observer).onChanged(
            MenusState(
                tagsLoading = TagsLoading.LOAD_MORE
            )
        )
        verify(observer).onChanged(
            MenusState(
                tagsLoading = null,
                tagsError = Errors.NO_MORE_OFFLINE_DATA
            )
        )
    }

    @Test
    fun `get items WHEN items repo return a list EXPECT pushing load then data state`() {
        mockItemsReposReturnValidData()

        menusViewModel.getItems(getMockedTag())

        verify(observer).onChanged(
            menusViewModel.getInitialState().copy(
                itemsLoading = true
            )
        )
        verify(observer).onChanged(
            menusViewModel.getInitialState().copy(
                items = itemsList
            )
        )
    }

    @Test
    fun `get items WHEN items repo return a no more offline throwable EXPECT pushing load then no more offline data state`() {
        `when`(itemsGeneralRepo.getItems(ArgumentMatchers.anyString()))
            .then { Single.error<Any>(NoMoreOfflineDataThrowable()) }
        menusViewModel.getItems(getMockedTag())

        verify(observer).onChanged(
            menusViewModel.getInitialState().copy(
                itemsLoading = true
            )
        )
        verify(observer).onChanged(
            menusViewModel.getInitialState().copy(
                itemErrors = Errors.NO_MORE_OFFLINE_DATA
            )
        )
    }

    private fun mockTagsReposReturnValidData() {
        `when`(tagsGeneralRepo.getTags()).then { Single.just(tagList) }
    }

    private fun mockItemsReposReturnValidData() {
        `when`(itemsGeneralRepo.getItems(ArgumentMatchers.anyString())).then { Single.just(tagList) }
    }

    private fun getMockedTag(): Tag {
        val tag = mock(Tag::class.java)
        `when`(tag.tagName).then { ArgumentMatchers.anyString() }
        return tag
    }
}