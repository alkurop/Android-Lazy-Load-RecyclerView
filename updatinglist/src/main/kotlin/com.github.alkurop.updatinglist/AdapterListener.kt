package com.github.alkurop.updatinglist

/**
 * Created by alkurop on 06.07.16.
 */
interface AdapterListener  <T> {
    fun addItems(items: List<T>, totalCount: Int)
    fun clearAdapter()
    fun onGetItemsError()
    fun onRetry() { }
    fun setLoading(isLoading: Boolean)
}