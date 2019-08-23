package com.example.restaurant.menus

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.restaurant.R
import com.example.restaurant.common.presentationLayer.BaseActivity
import com.example.restaurant.common.presentationLayer.EndlessRecyclerViewOnScrollListener
import com.example.restaurant.item.ItemDetailsActivity
import com.example.restaurant.menus.data.items.Item
import com.example.restaurant.menus.data.tags.Tag
import kotlinx.android.synthetic.main.activity_menus.*
import kotlinx.android.synthetic.main.partial_items_error.*
import javax.inject.Inject

class MenusActivity : BaseActivity(), TagsAdapter.ItemClickListener, ItemsAdapter.ItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    lateinit var menusViewModel: MenusViewModel
    @Inject
    lateinit var tagsAdapter: TagsAdapter
    lateinit var endlessRecyclerViewOnScrollListener: EndlessRecyclerViewOnScrollListener
    @Inject
    lateinit var itemsAdapter: ItemsAdapter

    override fun getContentResource(): Int = R.layout.activity_menus

    override fun init(state: Bundle?) {
        initViewModel()
        initTagsRecyclerView()
        initItemsRecyclerView()
        initSwipeLayout()
    }

    private fun render(menusState: MenusState) {
        with(menusState) {
            if (tagsError == null)
                tagsAdapter.addData(tags)
            else {
                Toast.makeText(
                    baseContext,
                    when (tagsError) {
                        Errors.NO_DATA_AVAILABLE -> getString(R.string.msg_no_available_data)
                        Errors.NO_MORE_OFFLINE_DATA -> getString(R.string.msg_no_more_offline_data)
                        else -> getString(R.string.msg_unknown_error)
                    },
                    Toast.LENGTH_LONG
                ).show()
                tagsAdapter.addRetryRow()
            }

            when (tagsLoading) {
                null -> pb_loading.visibility = View.GONE
                TagsLoading.LOAD_FROM_SCRATCH -> {
                    pb_loading.visibility = View.VISIBLE
                    endlessRecyclerViewOnScrollListener.restState()
                }
                TagsLoading.LOAD_MORE -> tagsAdapter.addLoadMoreRow()
            }

            pb_items_loading.visibility = if (itemsLoading) View.VISIBLE else View.GONE
            tv_items_initial.visibility = if (itemsInitialState) View.VISIBLE else View.GONE
            itemsAdapter.addData(items)
            if (itemErrors != null) {
                if (vs_items_error != null) {
                    vs_items_error.inflate()
                    btn_retry_items.setOnClickListener { menusViewModel.retryLoadItems() }
                } else
                    cl_items_error?.visibility = View.VISIBLE

                tv_items_error.text = when (itemErrors) {
                    Errors.NO_DATA_AVAILABLE -> getString(R.string.msg_no_available_data)
                    Errors.NO_MORE_OFFLINE_DATA -> getString(R.string.msg_no_more_offline_data)
                    else -> getString(R.string.msg_unknown_error)
                }
            } else {
                cl_items_error?.visibility = View.INVISIBLE
            }
        }
    }

    private fun initViewModel() {
        menusViewModel = ViewModelProvider(this, viewModelFactory).get(MenusViewModel::class.java)
        menusViewModel.liveData.observe(this, Observer {
            render(it)
        })
        menusViewModel.loadFromScratch()
    }

    private fun initTagsRecyclerView() {
        rv_items.isNestedScrollingEnabled = false
        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayout.HORIZONTAL, false)
        rv_tags.layoutManager = linearLayoutManager
        rv_tags.adapter = tagsAdapter
        endlessRecyclerViewOnScrollListener = object : EndlessRecyclerViewOnScrollListener() {

            override fun onLoadMore() {
                Log.d("PAGE_NUMBER", "LOAD")
                menusViewModel.loadMoreTags()
            }
        }
        rv_tags.addOnScrollListener(endlessRecyclerViewOnScrollListener)
    }

    private fun initItemsRecyclerView() {
        val linearLayoutManager = LinearLayoutManager(baseContext)
        rv_items.layoutManager = linearLayoutManager
        rv_items.adapter = itemsAdapter
    }

    private fun initSwipeLayout() {
        swipe_layout.setOnRefreshListener {
            swipe_layout.isRefreshing = false
            menusViewModel.refresh()
        }
    }

    override fun onRetryTagsClick() {
        menusViewModel.loadMoreTags()
    }

    override fun onTagClick(tag: Tag) {
        menusViewModel.getItems(tag)
    }

    override fun onItemMenuClick(item: Item, ivItemPhoto: ImageView) {
        ItemDetailsActivity.start(this, item.id_, ivItemPhoto)
    }
}
