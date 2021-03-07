package ir.mkhakpaki.videogames.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListLoadMoreListener : RecyclerView.OnScrollListener {

    private var visibleThreshold = Constants.LIST_THRESHOLD
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager?
    var ended: Boolean = false

    fun release() {
        mLayoutManager = null
    }

    fun setLoaded(loading: Boolean) {
        isLoading = loading
    }

    fun setThreshold(threshold: Int) {
        visibleThreshold = threshold
    }

    fun getLoaded(): Boolean {
        return isLoading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
    }

    constructor(layoutManager: LinearLayoutManager) {
        this.mLayoutManager = layoutManager
    }

    constructor(layoutManager: GridLayoutManager) {
        this.mLayoutManager = layoutManager
        visibleThreshold *= layoutManager.spanCount
    }


    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        if (dy <= 0 || isLoading) return

        totalItemCount = mLayoutManager?.itemCount ?: 0

        when (mLayoutManager) {
            is GridLayoutManager -> {
                lastVisibleItem = (mLayoutManager as GridLayoutManager).findLastVisibleItemPosition()
            }
            is LinearLayoutManager -> {
                lastVisibleItem = (mLayoutManager as LinearLayoutManager).findLastVisibleItemPosition()
            }
        }

        if (!ended && !isLoading && totalItemCount <= lastVisibleItem + visibleThreshold) {
            mOnLoadMoreListener.onLoadMore()
            isLoading = true
        }
    }

}

interface OnLoadMoreListener {
    fun onLoadMore()
}
