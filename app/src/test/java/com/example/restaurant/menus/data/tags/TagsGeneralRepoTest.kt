package com.example.restaurant.menus.data.tags

import com.example.restaurant.BaseTest
import com.example.restaurant.common.dataLayer.remote.error.ConnectionThrowable
import com.example.restaurant.menus.data.tags.errors.NoDataAvailableThrowable
import com.example.restaurant.menus.data.tags.local.TagLocalRepo
import com.example.restaurant.menus.data.tags.remote.TagsRemoteRepo
import io.reactivex.Completable
import io.reactivex.Maybe
import io.reactivex.Single
import org.junit.Assert.*
import org.junit.Test
import org.mockito.*
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
    fun `get tags WHEN local repo return empty list and remote repo return list EXPECT emit page`() {
        val mockedTagsList = listOf<Tag>(mock(Tag::class.java))
        `when`(tagLocalRepo.getTags(anyInt())).then { Maybe.just(listOf<Tag>()) }
        `when`(tagLocalRepo.insert(ArgumentMatchers.anyList())).then {Completable.complete()}
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.just(mockedTagsList) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo).insert(ArgumentMatchers.anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedTagsList)
        assertEquals(2, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN local repo return empty list and remote repo return empty list EXPECT emit no data available`() {
        `when`(tagLocalRepo.getTags(anyInt())).then { Maybe.just(listOf<Tag>()) }
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.just(listOf<Tag>()) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo, never()).insert(ArgumentMatchers.anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        assertEquals(1, observer.errorCount())
        observer.assertError(NoDataAvailableThrowable::class.java)
        assertEquals(1, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN local repo return empty list and remote repo return connection throwable EXPECT connection throwable`() {
        `when`(tagLocalRepo.getTags(anyInt())).then { Maybe.just(listOf<Tag>()) }
        `when`(tagsRemoteRepo.getTags(anyInt())).then { Single.error<List<Tag>>(ConnectionThrowable()) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo, times(0)).insert(ArgumentMatchers.anyList())
        verify(tagsRemoteRepo).getTags(anyInt())
        assertEquals(1, observer.errorCount())
        observer.assertError(ConnectionThrowable::class.java)
        assertEquals(1, tagsGeneralRepo.page)
    }

    @Test
    fun `get tags WHEN local repo return a list EXPECT emit page without calling remote repo`() {
        val mockedTagsList = listOf<Tag>(mock(Tag::class.java))
        `when`(tagLocalRepo.getTags(anyInt())).then { Maybe.just(mockedTagsList) }

        val observer = tagsGeneralRepo.getTags().test()

        verify(tagLocalRepo).getTags(anyInt())
        verify(tagLocalRepo, times( 0)).insert(ArgumentMatchers.anyList())
        verify(tagsRemoteRepo, never()).getTags(anyInt())
        observer.assertValueCount(1)
        observer.assertValueAt(0, mockedTagsList)
        assertEquals(2, tagsGeneralRepo.page)
    }
}