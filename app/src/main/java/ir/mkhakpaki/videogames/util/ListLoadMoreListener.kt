package ir.mkhakpaki.videogames.util

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ListLoadMoreListener(layoutManager: LinearLayoutManager) : RecyclerView.OnScrollListener() {

    private var visibleThreshold = Constants.LIST_THRESHOLD
    private lateinit var mOnLoadMoreListener: OnLoadMoreListener
    private var isLoading: Boolean = false
    private var lastVisibleItem: Int = 0
    private var totalItemCount: Int = 0
    private var mLayoutManager: RecyclerView.LayoutManager? = layoutManager
    var ended: Boolean = false

    fun release() {
        mLayoutManager = null
    }

    fun setLoaded(loading: Boolean) {
        isLoading = loading
    }

    fun setOnLoadMoreListener(mOnLoadMoreListener: OnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener
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
