package com.example.restaurant.menus.data.items

import com.example.restaurant.BaseTest
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.menus.data.items.local.ItemsLocalRepo
import com.example.restaurant.menus.data.items.remote.ItemsRemoteRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.errors.NoDataAvailableThrowable
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class ItemsGeneralRepoTest : BaseTest() {

    @Mock
    lateinit var itemsLocalRepo: ItemsLocalRepo
    @Mock
    lateinit var itemsRemoteRepo: ItemsRemoteRepo
    lateinit var itemsGeneralRepo: ItemsGeneralRepo

    override fun setup() {
        super.setup()
        itemsGeneralRepo = ItemsGeneralRepo(itemsLocalRepo, itemsRemoteRepo)
    }

    @Test
    fun `get items WHEN local repo return empty list and remote repo return list EXPECT emit list`() {
        val mockedItemsList = listOf<Item>(mock(Item::class.java))
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(listOf<Item>()) }
        `when`(itemsLocalRepo.insert(anyList())).then { Completable.complete() }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.just(mockedItemsList) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo, times(2)).getItems(anyString())
        verify(itemsLocalRepo).insert(anyList())
        verify(itemsRemoteRepo).getItems(anyString())
        observer.assertValueCount(1)
    }

    @Test
    fun `get items WHEN local repo return empty list and remote repo return empty list EXPECT emit no data available`() {
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(listOf<Tag>()) }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.just(listOf<Tag>()) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo).getItems(anyString())
        verify(itemsLocalRepo, never()).insert(ArgumentMatchers.anyList())
        verify(itemsLocalRepo).getItems(anyString())
        assertEquals(1, observer.errorCount())
        observer.assertError(NoDataAvailableThrowable::class.java)
    }


    @Test
    fun `get items WHEN local repo return empty list and remote repo return connection throwable EXPECT connection throwable`() {
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(listOf<Tag>()) }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo).getItems(anyString())
        verify(itemsLocalRepo, times(0)).insert(anyList())
        verify(itemsRemoteRepo).getItems(anyString())
        assertEquals(1, observer.errorCount())
        observer.assertError(ConnectionThrowable::class.java)
    }

    @Test
    fun `get items WHEN local repo return a list EXPECT emit list without calling remote repo`() {
        val mockedItemsList = listOf<Item>(mock(Item::class.java))
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(mockedItemsList) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo).getItems(anyString())
        verify(itemsLocalRepo, never()).insert(anyList())
        verify(itemsRemoteRepo, never()).getItems(anyString())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedItemsList)
    }
}