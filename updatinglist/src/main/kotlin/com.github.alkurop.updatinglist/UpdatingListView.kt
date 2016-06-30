package com.github.alkurop.updatinglist

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

class UpdatingListView : FrameLayout {
    val TAG = UpdatingListView::class.java.simpleName
    val recycler: RecyclerView
    val swipeView: SwipeRefreshLayout
    lateinit private var mAdapter: BaseLoadMoreAdapter<out Parcelable>
    private var progressView: View? = null
    private var emptyView: View? = null
    private var swipeRefreshListener: (() -> Unit)? = null
    private var loadMoreListener: ((offset: Int) -> Unit)? = null
    private var loadMorePagingListener: ((offset: Int) -> Unit)? = null

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = 0) : super(context, attrs, style) {
        recycler = RecyclerView(context, attrs)
        recycler.layoutManager = LinearLayoutManager(context)
        swipeView = SwipeRefreshLayout(context, attrs)
        addView(swipeView)
        swipeView.addView(recycler)
        swipeView.isEnabled = false
        swipeView.setOnRefreshListener { refresh() }
    }

    fun setLoadMoreListener(listener: ((offset: Int) -> Unit)?) {
        loadMoreListener = listener
        mAdapter.onLoadMoreListener = { listener?.invoke(mAdapter.getItemsSize()) }
    }

    fun setLoadMorePagingListener(listener: ((offset: Int) -> Unit)?) {
        loadMorePagingListener = listener
        mAdapter.onLoadMoreListener = { listener?.invoke(mAdapter.getItemsSize()) }
    }

    fun setSwipeRefreshListener(listener: ((() -> Unit)?)) {
        swipeRefreshListener = listener
        swipeView.isEnabled = listener != null
    }

    private fun refresh() {
        mAdapter.setLoadingMore(true)
        swipeRefreshListener?.invoke()
        ListLogger.log(TAG, "refresh")
    }

    private fun showEmpty(show: Boolean) {
        emptyView?.visibility = if (show) View.VISIBLE else View.GONE
        ListLogger.log(TAG, "showEmpty $show")
    }

    fun setAdapter(adapter: BaseLoadMoreAdapter<out Parcelable>) {
        mAdapter = adapter
        recycler.adapter = adapter
        if (loadMoreListener != null) {
            mAdapter.onLoadMoreListener = { loadMoreListener!!(mAdapter.getItemsSize()) }
        }
    }

    fun setLoadingViews(mProgressRes: Int, mEmptyRes: Int) {
        setProgressView(mProgressRes)
        setEmptyView(mEmptyRes)
    }

    fun setProgressView(mProgressRes: Int) {
        val progressView = LayoutInflater.from(context).inflate(mProgressRes, this, false)
        setProgressView(progressView)
    }

    fun setEmptyView(mEmptyRes: Int) {
        val emptyView = LayoutInflater.from(context).inflate(mEmptyRes, this, false)
        setEmptyView(emptyView)
    }

    fun setProgressView(mProgress: View) {
        this.progressView = mProgress
        addView(mProgress)
        mProgress.visibility = GONE
    }

    fun setEmptyView(mEmptyView: View) {
        this.emptyView = mEmptyView
        addView(mEmptyView)
        mEmptyView.visibility = GONE
    }

    fun onStop() {
        hideLoading()
        mAdapter.setLoadingMore(false)
    }

    fun setLogging(isLogging: Boolean) {
        ListLogger.allowLogging = isLogging
    }

    fun showLoading() {
        showEmpty(false)
        if (!swipeView.isRefreshing && (mAdapter.getItemsSize() == 0)) {
            progressView?.visibility = View.VISIBLE
        }
        ListLogger.log(TAG, "show loading")
    }

    fun hideLoading() {
        swipeView.isRefreshing = false
        progressView?.visibility = View.GONE
        showEmpty(mAdapter.itemCount == 0)
        ListLogger.log(TAG, "hide loading")
    }

    fun showLoading(isLoading: Boolean) {
        if (isLoading) showLoading() else hideLoading()
    }

    fun onError() {
        hideLoading()
        mAdapter.setLoadingMore(false)
    }

    override fun onSaveInstanceState(): Parcelable {
        val state = Bundle()
        state.putParcelable("superState", super.onSaveInstanceState());
        state.putSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY, ChildrenViewStateHelper (this).saveChildrenState())
        state.putParcelable("adapterModel", (recycler.adapter as BaseLoadMoreAdapter<*>).saveToModel())
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable("superState"))
            (recycler.adapter as BaseLoadMoreAdapter<*>).loadFromModel(state.getParcelable<AdapterStateModel>("adapterModel"))
            ChildrenViewStateHelper (this).restoreChildrenState(state.getSparseParcelableArray
            (ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY))

        } else {
            super.onRestoreInstanceState(state)
        }
    }

    override fun dispatchSaveInstanceState(container: SparseArray<Parcelable>?) {
        dispatchFreezeSelfOnly(container)
    }

    override fun dispatchRestoreInstanceState(container: SparseArray<Parcelable>?) {
        dispatchThawSelfOnly(container)
    }
}



