package com.example.restaurant.item

import androidx.lifecycle.Observer
import com.example.restaurant.BaseTest
import com.example.restaurant.item.data.GettingItemLocalRepo
import com.example.restaurant.menus.data.items.Item
import io.reactivex.Single
import org.junit.Test
import org.mockito.Mock
import org.mockito.Mockito.*

class ItemDetailsViewModelTest: BaseTest() {

    @Mock
    lateinit var gettingItemLocalRepo: GettingItemLocalRepo
    lateinit var itemDetailsViewModel: ItemDetailsViewModel
    @Mock
    lateinit var observer: Observer<ItemDetailsState>

    override fun setup() {
        super.setup()
        itemDetailsViewModel = ItemDetailsViewModel(gettingItemLocalRepo)
        itemDetailsViewModel.liveData.observeForever(observer)
    }

    @Test
    fun `load init WHEN repo return item EXPECTED push item state`() {
        val mockedItem =  mock(Item::class.java)
        `when`(gettingItemLocalRepo.getItem(anyInt())).then { Single.just(mockedItem) }

        itemDetailsViewModel.init(anyInt())

        verify(observer).onChanged(ItemDetailsState(mockedItem))
    }
}