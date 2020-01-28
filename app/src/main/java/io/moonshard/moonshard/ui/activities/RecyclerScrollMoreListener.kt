package io.moonshard.moonshard.ui.activities

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

class RecyclerScrollMoreListener(var layoutManager: LinearLayoutManager,
                                 var loadMoreListener: OnLoadMoreListener ):
    RecyclerView.OnScrollListener() {
    private var currentPage = 0
    private var previousTotalItemCount = 0
    private var loading = true



    private fun getLastVisibleItem(lastVisibleItemPositions: IntArray): Int {
        var maxSize = 0

        for (i in lastVisibleItemPositions.indices) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i]
            } else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i]
            }
        }
        return maxSize
    }

    override fun onScrolled(view: RecyclerView, dx: Int, dy: Int) {
        if (this.loadMoreListener != null) {
            var lastVisibleItemPosition = 0
            val totalItemCount = layoutManager.itemCount
             if (layoutManager is LinearLayoutManager) {
                lastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition()
            } else if (layoutManager is GridLayoutManager) {
                lastVisibleItemPosition = this.layoutManager.findLastVisibleItemPosition()
            }

            if (totalItemCount < this.previousTotalItemCount) {
                this.currentPage = 0
                this.previousTotalItemCount = totalItemCount
                if (totalItemCount == 0) {
                    this.loading = true
                }
            }

            if (this.loading && totalItemCount > this.previousTotalItemCount) {
                this.loading = false
                this.previousTotalItemCount = totalItemCount
            }

            val visibleThreshold = 5
            if (!this.loading && lastVisibleItemPosition + visibleThreshold > totalItemCount) {
                ++this.currentPage
                this.loadMoreListener.onLoadMore(
                    this.loadMoreListener.getMessagesCount(),
                    totalItemCount
                )
                this.loading = true
            }
        }

    }

    interface OnLoadMoreListener {
        fun getMessagesCount(): Int
        fun onLoadMore(var1: Int, var2: Int)
    }
}

