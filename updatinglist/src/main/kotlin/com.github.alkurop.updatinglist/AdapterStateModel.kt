package com.github.alkurop.updatinglist

import android.os.Parcelable

/**
 * Created by alkurop on 11.05.16.
 */
open class AdapterStateModel(
          var canLoadMore: Boolean,
          var lastAnimatedPosition: Int,
          var progressCount: Int,
          var currentPage: Int,
          var isLoading: Boolean,
          val items: MutableList<out Parcelable>) {
    open fun reset() {
        lastAnimatedPosition = 0
        canLoadMore = false
        currentPage = 0
        isLoading = false
        items.clear()
        progressCount = 0
    }

    companion object {

        fun getDefaultInstance() = AdapterStateModel(false, 0, 0, 0, false, mutableListOf())

    }
}