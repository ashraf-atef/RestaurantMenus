package com.example.restaurant.menus.data.tags

import com.example.restaurant.BaseTest
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.menus.data.errors.NoMoreOfflineDataThrowable
import com.example.restaurant.menus.data.tags.local.TagLocalRepo
import com.example.restaurant.menus.data.tags.remote.TagsRemoteRepo
import io.reactivex.Completable
import io.reactivex.Single
import org.junit.Assert.assertEquals
import org.junit.Test
import org.mockito.ArgumentMatchers
import org.mockito.Mock
import org.mockito.Mockito.*

class TagsGeneralRepoTest: BaseTest() {

    @Mock
    lateinit var tagLocalRepo: TagLocalRepo
    @Mock
    lateinit var tagsRemoteRepo: TagsRemoteRepo
    lateinit var tagsGeneralRepo: TagsGeneralRepo

    override fun setup() {
        super.setup()
        tagsGeneralRepo = TagsGeneralRepo(tagsRemoteRepo, tagLocalRepo)
    }

    @Test
    fun `rest EXPECT page number become 1`() {
        tagsGeneralRepo.rest()
        assertEquals(1, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN remote repo return list EXPECT emit page`() {
        val mockedTagsList = listOf<Tag>(mock(Tag::class.java))
        `when`(tagLocalRepo.insert(anyList())).then {Completable.complete()}
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.just(mockedTagsList) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo, never()).getTags(anyInt())
        verify(tagLocalRepo).insert(anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedTagsList)
        assertEquals(2, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN remote repo return connection error and local repo return empty list EXPECT no more offline throwable`() {
        `when`(tagLocalRepo.getTags(anyInt())).then { Single.just(listOf<Tag>()) }
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo, never()).insert(anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        assertEquals(1, observer.errorCount())
        observer.assertError(NoMoreOfflineDataThrowable::class.java)
        assertEquals(1, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN remote repo return connection error and local repo return list EXPECT emit page`() {
        val mockedTagsList = listOf<Tag>(mock(Tag::class.java))
        `when`(tagLocalRepo.getTags(anyInt())).then { Single.just(mockedTagsList) }
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo, never()).insert(ArgumentMatchers.anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedTagsList)
        assertEquals(2, tagsGeneralRepo.page)
    }
}