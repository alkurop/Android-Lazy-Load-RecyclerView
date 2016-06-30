package com.github.alkurop.updatinglist

import android.content.Context
import android.os.Parcelable
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout

class UpdatingListView : FrameLayout {
    val TAG = UpdatingListView::class.java.simpleName
    val recycler: RecyclerView
    val swipeView: SwipeRefreshLayout
    private var progressView: View? = null
    private var emptyView: View? = null
    lateinit private var mAdapter: BaseLoadMoreAdapter<out Parcelable>

    var swipeRefreshListener: (() -> Unit)? = null
        set(value) {
            field = value;  swipeView.isEnabled = value != null
        }

    var loadMoreListener: ((offset: Int) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.onLoadMoreListener = { value?.invoke(mAdapter.getItemsSize()) }

        }
    var loadMorePagingListener: ((offset: Int) -> Unit)? = null
        set(value) {
            field = value
            mAdapter.onLoadMoreListener = { value?.invoke(mAdapter.getItemsSize()) }
        }

    @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, style: Int = 0) : super(context, attrs, style) {
        recycler = RecyclerView(context, attrs)
        recycler.layoutManager = LinearLayoutManager(context)
        swipeView = SwipeRefreshLayout(context, attrs)
        addView(swipeView)
        swipeView.addView(recycler)
        swipeView.isEnabled = false
        swipeView.setOnRefreshListener { refresh() }
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
        setEmptyView(progressView)
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
        showLoading(false)
        mAdapter.setLoadingMore(false)
    }

    override fun onSaveInstanceState(): Parcelable {
        return UpdatingListSaveState(super.onSaveInstanceState(), (recycler.adapter as BaseLoadMoreAdapter<*>).saveToModel())
    }

    override fun onRestoreInstanceState(state: Parcelable) {
        if (state !is UpdatingListSaveState) {
            super.onRestoreInstanceState(state);
            return;
        }
        super.onRestoreInstanceState(state);
        (recycler.adapter as BaseLoadMoreAdapter<*>).loadFromModel(state.adapterState)
    }

    fun showLoading(isLoading: Boolean) {
        if (!isLoading) {
            swipeView.isRefreshing = false
            progressView?.visibility = View.GONE
            showEmpty(mAdapter.itemCount == 0)
        } else {
            showEmpty(false)
            if (!swipeView.isRefreshing && (mAdapter.getItemsSize() == 0)) {
                progressView?.visibility = View.VISIBLE
            }
        }
        ListLogger.log(TAG, "isLoading $isLoading")
    }

    fun onError() {
        showLoading(false)
        mAdapter.setLoadingMore(false)
    }
}



