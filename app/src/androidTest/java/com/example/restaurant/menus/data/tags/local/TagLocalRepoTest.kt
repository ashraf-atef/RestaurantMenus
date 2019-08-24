package com.example.restaurant.menus.data.tags.local

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.restaurant.InMemoryLocalDatabase
import com.example.restaurant.common.dataLayer.local.LocalDatabase
import com.example.restaurant.menus.data.tags.Tag
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TagLocalRepoTest {

    lateinit var tagsLocalRepo: TagLocalRepo
    lateinit var localDatabase: LocalDatabase
    private val tag: Tag by lazy {
        Tag("Dessert", "")
    }

    @Before
    fun init() {
        localDatabase = InMemoryLocalDatabase().localDatabase
        tagsLocalRepo = TagLocalRepo(localDatabase.tagsDao())
    }

    @Test
    fun insertItems_completeSuccessfully() {
        val observer = tagsLocalRepo.insert(listOf(tag)).test()

        observer.assertComplete()
    }

    @Test
    fun getTagsInFirstPage_tableIsNotEmpty_emitFirstPage() {
        tagsLocalRepo.insert(listOf(tag)).subscribe()

        val observer = tagsLocalRepo.getTags(1).test()

        observer.assertValueCount(1)
        observer.assertValueAt(0, listOf(tag))
        observer.assertComplete()
    }

    @Test
    fun getTagsInFirstPage_tableIsEmpty_emitEmptyPage() {
        val observer = tagsLocalRepo.getTags(1).test()

        observer.assertValueCount(1)
        observer.assertValueAt(0, listOf())
        observer.assertComplete()
    }

    @After
    fun close() {
        localDatabase.close()
    }
}