package com.example.restaurant.common.presentationLayer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewOnScrollListener : RecyclerView.OnScrollListener() {
    /**
     * The total number of items in the data after the last load
     */
    private var previousTotal = 0
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private var isLoading = true

    private val loadMoreRunnable = Runnable { this.onLoadMore() }

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        if (dx > 0) { // scrolling end (right / left depend on locale)
            val visibleItemCount = recyclerView.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (isLoading) {
                // the + 1 is for the error / progress layouts
                if (totalItemCount > previousTotal + 1) {
                    isLoading = false
                    previousTotal = totalItemCount
                }
            }
            val visibleThreshold = 5
            if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                // End has been reached
                recyclerView.post(loadMoreRunnable)
                isLoading = true
            }
        }
    }

    abstract fun onLoadMore()

    fun restState() {
        previousTotal = 0
        isLoading = true
    }
}