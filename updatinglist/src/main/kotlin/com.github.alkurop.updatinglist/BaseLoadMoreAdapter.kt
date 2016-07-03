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
abstract class BaseLoadMoreAdapter<T : Parcelable>() : RecyclerView.Adapter<BaseViewHolder<T>>() {
    private val VIEW_TYPE = -300
    private val PROGRESS_TYPE = -400
    private val LOADING_OFFSET = 3
    private var mRecycler: RecyclerView? = null
    private val TAG = BaseLoadMoreAdapter::class.java.simpleName
    var onLoadMoreListener: (() -> Unit)? = null
    var onLoadMorePagingListener: (() -> Unit)? = null
    var state: AdapterStateModel = AdapterStateModel.getDefaultInstance()

    abstract fun onCreateProgressVH(viewGroup: ViewGroup): BaseViewHolder<T>

    abstract fun onCreateVH(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<T>

    open fun getAnimationRes() = R.anim.slide_in_bottom

    fun clear() {
        state.reset()
        notifyDataSetChanged()
    }
    @SuppressWarnings("unchecked")
    fun getItems(): MutableList<T> = state.items as MutableList<T>

    fun getItemsSize() = state.items.size

    open fun addItems(newItems: List<T>) {
        setLoadingMore(false)
        val oldSize = getItemsSize()
        val delta = newItems.size
        getItems().addAll(newItems)
        notifyItemRangeInserted(oldSize, delta - 1)
        state.currentPage += 1
        ListLogger.log(TAG, "add items ${newItems.size}")
    }

    fun setCanLoadMore(canLoadMore: Boolean) {
        state.canLoadMore = canLoadMore
    }

    fun addItem(item: T) {
        addItemToPosition(item, getItemsSize())
    }

    fun addItemToPosition(item: T, position: Int) {
        setLoadingMore(false)
        getItems().add(position, item)
        notifyItemInserted(position)
        ListLogger.log(TAG, "add item");
    }

    fun getItem(position: Int): T? {
        if (getItemsSize() <= position) {
            return null
        }
        return getItems()[position]
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
        if (getItemsSize() > position) {
            viewHolder.bind(getItems()[position])
        }
        setAnimation(viewHolder.container, position)
    }

    override fun getItemCount(): Int {
        return if (getItemsSize() > 0) getItemsSize() + state.progressCount else 0
    }

    override fun getItemViewType(position: Int): Int {
        if (position < getItemsSize())
            return VIEW_TYPE
        return PROGRESS_TYPE
    }

    override fun onViewDetachedFromWindow(holder: BaseViewHolder<T>) {
        holder.container.clearAnimation()
        super.onViewDetachedFromWindow(holder)
    }

    open fun setLoadingMore(isLoading: Boolean) {
        if (isLoading) showLoadMore()
        else hideLoadMore()

        ListLogger.log(TAG, "showloading , $isLoading")
    }

    fun showLoadMore() {
        if (getItemsSize() > 0) {
            state.isLoading = true
            state.progressCount = 1
            notifyItemInserted(itemCount - 1)
        }
    }

    fun hideLoadMore() {
        state.progressCount = 0
        state.isLoading = false
        notifyItemRemoved(getItemsSize())
    }

    open fun loadFromModel(stateModel: AdapterStateModel) {
        this.state = stateModel
        ListLogger.log(TAG, "loadFromModel , ${stateModel.toString()}")
        mRecycler?.scrollToPosition(state.scrollPosition)
    }

    fun saveToModel(): AdapterStateModel {
        var pos = (mRecycler?.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        if (pos >= LOADING_OFFSET)
            pos -= LOADING_OFFSET
        state.scrollPosition = pos
        return state
    }

    private fun setOnScrollListener(recycler: RecyclerView) {
        recycler.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val count = (recycler.layoutManager?.itemCount ?: 0) - 1
                var lastItem = (recycler.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
                if (lastItem + LOADING_OFFSET <= count) {
                    lastItem += LOADING_OFFSET
                }
                if (count == lastItem && !state.isLoading && state.canLoadMore) {
                    ListLogger.log(TAG, "load more")
                    onLoadMoreListener?.invoke()
                    onLoadMorePagingListener?.invoke()
                    if (onLoadMoreListener != null || onLoadMorePagingListener != null) {
                        setLoadingMore(true)
                    }
                }
            }
        })
    }

    private fun setAnimation(container: View, position: Int) {
        if (getAnimationRes() != 0) {
            if (position + 1 > state.lastAnimatedPosition) {
                val animation = AnimationUtils.loadAnimation(container.context, getAnimationRes());
                container.startAnimation(animation);
                state.lastAnimatedPosition = position + 1;
            }
        }
    }

    inner class ProgressVH(itemView: View) : BaseViewHolder<T>(itemView)
}

abstract class BaseViewHolder<T>(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var container = itemView
    var data: T? = null
    open fun bind(data: T) {
        this.data = data
    }
}












