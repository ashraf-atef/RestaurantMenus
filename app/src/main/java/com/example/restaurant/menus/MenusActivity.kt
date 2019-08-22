package com.example.restaurant.menus

import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurant.R
import com.example.restaurant.common.presentationLayer.BaseActivity
import com.example.restaurant.common.presentationLayer.EndlessRecyclerViewOnScrollListener
import kotlinx.android.synthetic.main.activity_menus.*
import javax.inject.Inject

class MenusActivity : BaseActivity(), TagsAdapter.ItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    lateinit var menusViewModel: MenusViewModel

    @Inject
    lateinit var tagsAdapter: TagsAdapter

    lateinit var endlessRecyclerViewOnScrollListener: EndlessRecyclerViewOnScrollListener

    override fun getContentResource(): Int = R.layout.activity_menus

    override fun init(state: Bundle?) {
        menusViewModel = ViewModelProvider(this, viewModelFactory).get(MenusViewModel::class.java)
        menusViewModel.liveData.observe(this, Observer {
            render(it)
        })
        menusViewModel.loadFromScratch()

        val linearLayoutManager = LinearLayoutManager(baseContext, LinearLayout.HORIZONTAL, false)
        rv_data.layoutManager = linearLayoutManager
        rv_data.adapter = tagsAdapter
        endlessRecyclerViewOnScrollListener = object : EndlessRecyclerViewOnScrollListener() {

            override fun getLayoutManager(): RecyclerView.LayoutManager = linearLayoutManager

            override fun onLoadMore() {
                menusViewModel.loadMore()
            }
        }
        rv_data.addOnScrollListener(endlessRecyclerViewOnScrollListener)

        swipe_layout.setOnRefreshListener {
            swipe_layout.isRefreshing = false
            menusViewModel.refresh()
        }
    }

    private fun render(menusState: MenusState) {
        with(menusState) {
            if (error == null)
                tagsAdapter.addData(tagList)
            else {
                Toast.makeText(
                    baseContext,
                    when (error) {
                        DataErrors.NO_DATA_AVAILABLE -> getString(R.string.msg_no_available_data)
                        DataErrors.NO_MORE_OFFLINE_DATA -> getString(R.string.msg_no_more_offline_data)
                        else -> getString(R.string.msg_unknown_error)
                    },
                    Toast.LENGTH_LONG
                ).show()
                tagsAdapter.addRetryRow()
            }

            if (loading == null) {
                pb_loading.visibility = View.INVISIBLE
            } else {
                when(loading) {
                    null ->  pb_loading.visibility = View.INVISIBLE
                    DataLoading.LOAD_FROM_SCRATCH -> {
                        pb_loading.visibility = View.VISIBLE
                        endlessRecyclerViewOnScrollListener.restState()
                    }
                    DataLoading.LOAD_MORE ->  tagsAdapter.addLoadMoreRow()
                }
            }
        }
    }

    override fun onRetryClick() {
        menusViewModel.loadMore()
    }
}
