package com.example.restaurant.common.presentationLayer;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class EndlessRecyclerViewOnScrollListener extends RecyclerView.OnScrollListener {
    /**
     * The total number of items in the data after the last load
     */
    private int previousTotal = 0;
    /**
     * True if we are still waiting for the last set of data to load.
     */
    private boolean isLoading = true;

    private final Runnable loadMoreRunnable = this::onLoadMore;

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        if (dx > 0) { // scrolling down
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getLayoutManager().getItemCount();
            int firstVisibleItem = ((LinearLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();

            if (isLoading) {
                if (totalItemCount > previousTotal + 1) { // the + 1 is for the error / progress layouts
                    isLoading = false;
                    previousTotal = totalItemCount;
                }
            }
            int visibleThreshold = 5;
            if (!isLoading && (totalItemCount - visibleItemCount)
                    <= (firstVisibleItem + visibleThreshold)) {
                // End has been reached

                recyclerView.post(loadMoreRunnable);

                isLoading = true;
            }
        }
    }


    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore();

    public void restState() {
        previousTotal = 0;
        isLoading = true;
    }
}