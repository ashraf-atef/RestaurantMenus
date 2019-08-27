package com.example.restaurant.menus.data.items

import com.example.restaurant.BaseTest
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.menus.data.items.local.ItemsLocalRepo
import com.example.restaurant.menus.data.items.remote.ItemsRemoteRepo
import com.example.restaurant.menus.data.tags.Tag
import com.example.restaurant.menus.data.errors.NoMoreOfflineDataThrowable
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test
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
    fun `get items WHEN remote repo return list EXPECT emit page`() {
        val mockedItemsList = listOf<Item>(mock(Item::class.java))
        `when`(itemsLocalRepo.insert(anyList())).then { Completable.complete() }
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(mockedItemsList) }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.just(mockedItemsList) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsRemoteRepo).getItems(anyString())
        verify(itemsLocalRepo).insert(anyList())
        verify(itemsLocalRepo).getItems(anyString())
        observer.assertValueCount(1)
    }

    @Test
    fun `get items WHEN local repo return empty list and remote repo return connection throwable EXPECT no more offline throwable`() {
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(listOf<Tag>()) }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo).getItems(anyString())
        verify(itemsLocalRepo, never()).insert(anyList())
        verify(itemsRemoteRepo).getItems(anyString())
        assertEquals(1, observer.errorCount())
        observer.assertError(NoMoreOfflineDataThrowable::class.java)
    }

    @Test
    fun `get items WHEN remote repo return connection error and local repo return list EXPECT emit page`() {
        val mockedItemsList = listOf<Item>(mock(Item::class.java))
        `when`(itemsLocalRepo.getItems(anyString())).then { Single.just(mockedItemsList) }
        `when`(itemsRemoteRepo.getItems(anyString())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = itemsGeneralRepo.getItems(anyString()).test()

        verify(itemsLocalRepo).getItems(anyString())
        verify(itemsLocalRepo, never()).insert(anyList())
        verify(itemsRemoteRepo).getItems(anyString())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedItemsList)
    }
}