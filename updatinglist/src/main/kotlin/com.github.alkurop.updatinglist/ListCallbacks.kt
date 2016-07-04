package com.github.alkurop.updatinglist

import android.widget.ImageView

/**
 * Created by alkurop on 08.06.16.
 */
interface AdapterListener  <T> {
    fun addItems(items: List<T>, totalCount: Int)
    fun clearAdapter()
    fun onGetItemsError()
    fun onRetry() {
    }

    fun setLoading(isLoading: Boolean)
}

interface AdapterController <T> {
    fun onRefresh() {
    }

    fun onAdapterGetMoreItems(offset: Int) {
    }

    fun onAdapterItemClick(item: T) {
    }

    fun onAdapterItemClick(position: Int) {
    }
    fun onAdapterItemClick(item: T, imageView: ImageView) {
    }
}