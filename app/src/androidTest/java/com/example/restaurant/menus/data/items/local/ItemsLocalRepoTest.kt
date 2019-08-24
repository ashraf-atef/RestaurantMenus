package com.example.restaurant.menus.data.items.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.restaurant.InMemoryLocalDatabase
import com.example.restaurant.common.dataLayer.local.LocalDatabase
import com.example.restaurant.menus.data.items.Item
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ItemsLocalRepoTest {

    lateinit var itemsLocalRepo: ItemsLocalRepo
    lateinit var localDatabase: LocalDatabase
    private val item: Item by lazy {
        Item(1, 300, "Dessert", "Burger", "", "")
    }

    @Before
    fun init() {
        localDatabase = InMemoryLocalDatabase().localDatabase
        itemsLocalRepo = ItemsLocalRepo(localDatabase.itemsDao())
    }

    @Test
    fun insertItems_completeSuccessfully() {
        val observer = itemsLocalRepo.insert(listOf(item)).test()

        observer.assertComplete()
    }

    @Test
    fun getItemsByTag_tableIsNotEmptyAndTagNameIsCorrect_emitItems() {
        itemsLocalRepo.insert(listOf(item)).subscribe()

        val observer = itemsLocalRepo.getItems(item.tagName).test()

        observer.assertValueCount(1)
        observer.assertValueAt(0, listOf(item))
        observer.assertComplete()
    }
    @Test
    fun getItemsByTag_tableIsNotEmptyAndTagNameIsWrong_emitEmptyList() {
        itemsLocalRepo.insert(listOf(item)).subscribe()

        val observer = itemsLocalRepo.getItems("").test()

        observer.assertValueCount(1)
        observer.assertValueAt(0, listOf())
        observer.assertComplete()
    }
    
    @Test
    fun getItemsByTag_tableIstEmpty_emitEmptyList() {
        val observer = itemsLocalRepo.getItems("").test()

        observer.assertValueCount(1)
        observer.assertValueAt(0, listOf())
        observer.assertComplete()
    }

    @After
    fun close() {
        localDatabase.close()
    }
}