package com.github.alkurop.updatinglist

import android.os.Parcelable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils

/**
 * Created by alkurop on 26.04.16.
 * Set canLoadMore and isLoading when setting adapter items
 */
abstract class BaseLoadMoreAdapter <T : Parcelable>() : RecyclerView.Adapter<BaseViewHolder<T>>() {
    private val VIEW_TYPE = -300
    private val PROGRESS_TYPE = -400
    var onLoadMoreListener: (() -> Unit)? = null
    var onLoadMorePagingListener: (() -> Unit)? = null
    val realSize: Int
        get() = mState.items.size

    val controller: AdapterController
    private lateinit var mState: AdapterStateModel
    private var mRecycler: RecyclerView? = null
    private val TAG = BaseLoadMoreAdapter::class.java.simpleName

    init {
        controller = AdapterController(this)
        mState = AdapterStateModel(true, 0, 0, 0, 0, false, false, mutableListOf())
    }

    abstract fun onCreateProgressVH(viewGroup: ViewGroup): BaseViewHolder<T>

    abstract fun onCreateVH(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<T>

    open fun getAnimationRes() = R.anim.slide_in_bottom

    open fun getLoadingOffset() = 3

    open fun getRestoreFocusOffset() = 3

    fun clear() {
        mState.currentPage = 0
        mState.items.clear()
        mState.canLoadMore = true
        mState.isError = false
        setLoading(false)
        notifyDataSetChanged()
    }

    fun addItems(newItems: List<T>, canLoadMore: Boolean) {
        setLoading(false)
        val oldSize = mState.items.size
        val delta = newItems.size

        mState.items.addAll(newItems)
        mState.canLoadMore = false
        notifyItemRemoved(oldSize)
        notifyItemRangeInserted(oldSize, delta - 1)
        if (newItems.size > 0) {
            mState.canLoadMore = canLoadMore
            mState.currentPage += 1
        } else {
            mState.canLoadMore = false
        }
        ListLogger.log(TAG, "add items ${newItems.size}")
    }

    fun getItem(position: Int): T? {
        if (mState.items.size <= position) {
            return null
        }
        return mState.items[position] as T
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.mRecycler = recyclerView
        setOnScrollListener(recyclerView)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView?) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.mRecycler = null
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<T>? {
        if (viewType == PROGRESS_TYPE) {
            return onCreateProgressVH(viewGroup)
        }
        return onCreateVH(viewGroup, viewType)
    }

    override fun onBindViewHolder(viewHolder: BaseViewHolder<T>, position: Int) {
        if (mState.items.size > position) {
            viewHolder.bind(mState.items[position] as T)
        }
        setAnimation(viewHolder.container, position)
    }

    override fun getItemCount(): Int {
        return if (mState.items.size > 0) mState.items.size + mState.progressCount else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position < mState.items.size)
            return VIEW_TYPE
        return PROGRESS_TYPE
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        holder.container.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    private fun setLoading(value: Boolean) {
        mState.isLoading = value
        if (value && mState.items.size > 0) {
            mState.progressCount = 1
            notifyItemInserted(itemCount - 1)
        } else {
            mState.progressCount = 0
        }
        ListLogger.log(TAG, "showloading , $value")
    }

    private fun onError() {
        mState.canLoadMore = false
        mState.isError = true
        if (mState.isLoading) {
            mState.isLoading = false
            notifyDataSetChanged()
        }
        ListLogger.log(TAG, "onError")
    }

    private fun onRetry() {
        mState.canLoadMore = true
        mState.isError = false
        notifyDataSetChanged()
        ListLogger.log(TAG, "onRetry")
    }

    private fun loadFromModel(stateModel: AdapterStateModel) {
        this.mState = stateModel
        this.mState.isLoading = false
        ListLogger.log(TAG, "loadFromModel , ${stateModel.toString()}")
        mRecycler?.scrollToPosition(mState.scrollPosition)
    }

    private fun saveToModel(): AdapterStateModel {
        var pos = (mRecycler?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (pos >= getRestoreFocusOffset())
            pos -= getRestoreFocusOffset()
        mState.scrollPosition = pos
        return mState
    }

    private fun setOnScrollListener(recycler: RecyclerView) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val count = (recycler.layoutManager?.itemCount ?: 0) - 1
                var lastItem = (recycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastItem + getLoadingOffset() <= count) {
                    lastItem += getLoadingOffset()
                }
                if (count == lastItem && !mState.isLoading && mState.canLoadMore) {
                    ListLogger.log(TAG, "load more")
                    onLoadMoreListener?.invoke()
                    onLoadMorePagingListener?.invoke()
                    if (onLoadMoreListener != null) {
                        setLoading(true)
                    }
                }
            }
        })
    }

    private fun setAnimation(container: View, position: Int) {
        if (getAnimationRes() != 0) {
            if (position + 1 > mState.lastAnimatedPosition) {
                var animation = AnimationUtils.loadAnimation(container.context, getAnimationRes());
                container.startAnimation(animation);
                mState.lastAnimatedPosition = position + 1 ;
            }
        }
    }

    inner class ProgressVH(itemView: View) : BaseViewHolder<T>(itemView)

    inner class AdapterController(private val adapter: BaseLoadMoreAdapter<*>) {

        fun getAdapterState() = mState

        fun setLoading(value: Boolean) = adapter.setLoading(value)

        fun onError() = adapter.onError()

        fun onRetry() = adapter.onRetry()

        fun saveToModel(): AdapterStateModel = adapter.saveToModel()

        fun loadFromModel(stateModel: AdapterStateModel) = adapter.loadFromModel(stateModel)
    }
}

abstract class BaseViewHolder <T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var container = itemView
    var data: T? = null
    open fun bind(data: T) {
        this.data = data
    }
}












