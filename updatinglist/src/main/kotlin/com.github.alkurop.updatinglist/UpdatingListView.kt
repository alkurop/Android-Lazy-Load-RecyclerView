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

open class UpdatingListView : FrameLayout {
    val TAG = UpdatingListView::class.java.simpleName
    val recycler: RecyclerView
    val swipeView: SwipeRefreshLayout
    lateinit var mAdapter: BaseLoadMoreAdapter<out Parcelable>
    private var mProgressView: View? = null
    private var mEmptyView: View? = null
    private var swipeRefreshListener: (() -> Unit)? = null
    private var loadMoreListener: ((offset: Int) -> Unit)? = null
    private var loadMorePagingListener: ((offset: Int) -> Unit)? = null

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = 0) : super(context, attrs, style) {
        View.inflate(context, R.layout.list_layout, this)
        recycler = findViewById(R.id.recyclerView) as RecyclerView
        recycler.layoutManager = LinearLayoutManager(context)
        swipeView = findViewById(R.id.swipeRefresh) as SwipeRefreshLayout
        swipeView.isEnabled = false
        swipeView.setOnRefreshListener { refresh() }
        if (id == View.NO_ID)
            id = 1011012
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
        swipeRefreshListener?.invoke()
        ListLogger.log(TAG, "refresh")
    }

    private fun showEmpty(show: Boolean) {
        mEmptyView?.visibility = if (show) View.VISIBLE else View.GONE
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
        this.mProgressView = mProgress
        addView(mProgress)
        mProgress.visibility = GONE
    }

    fun setEmptyView(mEmptyView: View) {
        this.mEmptyView = mEmptyView
        addView(mEmptyView)
        mEmptyView.visibility = GONE
    }

    open fun stopLoading() {
        hideLoading()
        if (mAdapter.state.isLoading)
            mAdapter.setLoadingMore(false)
    }

    fun setLogging(isLogging: Boolean) {
        ListLogger.allowLogging = isLogging
    }

    fun showLoading() {
        showEmpty(false)
        if (!swipeView.isRefreshing && (mAdapter.getItemsSize() == 0)) {
            mProgressView?.visibility = View.VISIBLE
        }
        ListLogger.log(TAG, "show loading")
    }

    open fun hideLoading() {
        swipeView.isRefreshing = false
        mProgressView?.visibility = View.GONE
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
        stopLoading()
        val state = Bundle()
        state.putParcelable("superState", super.onSaveInstanceState());
        state.putSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY, ChildrenViewStateHelper(this).saveChildrenState())
        state.putParcelable("adapterModel", (recycler.adapter as BaseLoadMoreAdapter<*>).saveToModel())
        return state
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state is Bundle) {
            super.onRestoreInstanceState(state.getParcelable("superState"))
            (recycler.adapter as BaseLoadMoreAdapter<*>).loadFromModel(state.getParcelable<AdapterStateModel>("adapterModel"))
            ChildrenViewStateHelper(this).restoreChildrenState(state.getSparseParcelableArray(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY))
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

    //methods used in tests
    fun getEmptyView() = mEmptyView

    fun getProgressView() = mProgressView
}



