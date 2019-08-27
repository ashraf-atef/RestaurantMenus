package com.example.restaurant.common.presentationLayer

import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

abstract class EndlessRecyclerViewOnScrollListener : RecyclerView.OnScrollListener() {

    private var total = 0
    private var isLoading = true

    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)
        // Scroll horizontal to the end
        if (dx > 0) {
            val visibleItemCount = recyclerView.childCount
            val totalItemCount = recyclerView.layoutManager!!.itemCount
            val firstVisibleItem = (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()

            if (isLoading) {
                // Increment total with one to handle adding loading and error items
                if (totalItemCount > total + 1) {
                    isLoading = false
                    total = totalItemCount
                }
            }

            val visibleThreshold = 5
            // Check if the end has been reached before 5 items (visibleThreshold)
            if (!isLoading && totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                onLoadMore()
                isLoading = true
            }
        }
    }

    abstract fun onLoadMore()

    fun restState() {
        total = 0
        isLoading = true
    }
}