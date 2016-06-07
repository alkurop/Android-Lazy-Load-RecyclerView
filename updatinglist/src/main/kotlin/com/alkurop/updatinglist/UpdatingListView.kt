package com.alkurop.updatinglist

import android.content.Context
import android.os.Parcelable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

/**
 * Created by alkurop on 26.04.16.
 *
 *
 * Use UpdatingList with BaseLoadMoreAdapter
 *
 * setLoadingViews(_,_) should be called to pass the desired LoadingView and EmptyList view
 *
 * Use either LoadMoreListener - it returns item count
 * or LoadMorePaging listener - it returns current adapter page (set adapter.currentPage when adding items and
 * adapter will return it)
 *
 *
 * Set refreshing listener to use SwipeRefresh . If no set, swipe to refresh will be disabled
 *
 *
 * UpdatingList can save and restore it's state on configuration change
 * In this case the view must have an id
 */


class UpdatingListView : FrameLayout {
    val TAG = UpdatingListView::class.java.simpleName
    val recycler: RecyclerView
    val swipeView: SwipeRefreshLayout
    var swipeRefreshListener: (() -> Unit)? = null
        set(value) {
            field = value;  swipeView.isEnabled = value != null
        }
    var adapter: BaseLoadMoreAdapter<out Parcelable>? = null
        set(value) {
            field = value; doOnSetAdapter(value)
        }
    var isLoading: Boolean
        set(value) = doOnSetLoading(value)
        get() = adapter?.controller?.getAdapterState()?.isLoading ?: false

    var loadMoreListener: ((offset: Int) -> Unit)? = null
        set(value) {
            field = value; if (value != null ) doOnSetLoadMoreListener (value)
        }
    var loadMorePagingListener: ((offset: Int) -> Unit)? = null
        set(value) {
            if (value != null ) doOnSetLoadMorePagingListener(value)
        }

    private var progressView: View? = null
    private var emptyView: View? = null

    constructor(context: Context) : super(context) {
        swipeView = SwipeRefreshLayout(context)
        recycler = RecyclerView(context)
        setUp()

    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        swipeView = SwipeRefreshLayout(context, attrs)
        recycler = RecyclerView(context, attrs)
        setUp()

    }

    private fun setUp() {
        recycler.layoutManager = LinearLayoutManager(context)
        addView(swipeView)
        swipeView.addView(recycler)
        swipeView.isEnabled = false
        swipeView.setOnRefreshListener { adapter?.clear(); refresh() }
    }

    override fun onSaveInstanceState(): Parcelable {
        return UpdatingListSaveState(super.onSaveInstanceState(),
                  (recycler.adapter as BaseLoadMoreAdapter<*>).controller.saveToModel())
    }

    override fun onRestoreInstanceState(state: Parcelable) {
           if (state !is UpdatingListSaveState) {
            super.onRestoreInstanceState(state);
            return;
        }
        super.onRestoreInstanceState(state);
        (recycler.adapter as BaseLoadMoreAdapter<*>).controller.loadFromModel(state.adapterState)
    }

    fun showLoading(isLoading: Boolean) {
        if (!isLoading) {
            adapter?.controller?.setLoading(false)
            swipeView.isRefreshing = false
            progressView?.visibility = View.GONE
            showEmpty(adapter?.itemCount == 0)
        } else {
            showEmpty(false)
            if (!swipeView.isRefreshing && (adapter == null || adapter?.realSize == 0)) {
                progressView?.visibility = View.VISIBLE
            }
        }
        ListLogger.log(TAG, "isLoading $isLoading")
    }

    fun onError() {
        showLoading(false)
        adapter?.controller?.onError()
    }

    fun onRetry() = adapter?.controller?.onRetry()

    fun setLoadingViews(mProgress: View, mEmptyView: View) {
        this.progressView = mProgress
        this.emptyView = mEmptyView
        addView(mProgress)
        addView(mEmptyView)
        mProgress.visibility = GONE
        mEmptyView.visibility = GONE
    }

    fun setLoadingViews(mProgressRes: Int, mEmptyRes: Int) {
        val progressView = LayoutInflater.from(context).inflate(mProgressRes, this, false)
        val emptyView = LayoutInflater.from(context).inflate(mEmptyRes, this, false)
        setLoadingViews(progressView, emptyView)
    }


    private fun doOnSetLoading(value: Boolean) {
        showLoading(value)
        ListLogger.log(TAG, "loading $value")
    }

    private fun doOnSetAdapter(value: BaseLoadMoreAdapter<out Parcelable>?) {
        recycler.adapter = value
        if (loadMoreListener != null) {
            adapter?.onLoadMoreListener = { loadMoreListener!!(adapter!!.realSize) }
        }
    }

    private fun doOnSetLoadMoreListener(callback: (offset: Int) -> Unit) {
        var adapter = recycler.adapter
        if (adapter != null && adapter is BaseLoadMoreAdapter<*>)
            adapter.onLoadMoreListener = { callback(adapter.realSize) }
    }

    private fun doOnSetLoadMorePagingListener(callback: (offset: Int) -> Unit) {
        var adapter = recycler.adapter
        if (adapter != null && adapter is BaseLoadMoreAdapter<*>)
            adapter.onLoadMorePagingListener = { callback(adapter.controller.getAdapterState().currentPage) }
    }

    private fun refresh() {
        adapter?.controller?.setLoading(true)
        swipeRefreshListener?.invoke()
        ListLogger.log(TAG, "refresh")
    }

    private fun showEmpty(show: Boolean) {
        emptyView?.visibility = if (show) View.VISIBLE else View.GONE
        ListLogger.log(TAG, "showEmpty $show")
    }
}



