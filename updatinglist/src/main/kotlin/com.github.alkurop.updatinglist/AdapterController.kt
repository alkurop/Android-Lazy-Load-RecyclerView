package com.github.alkurop.updatinglist

import android.widget.ImageView

/**
 * Created by alkurop on 08.06.16.
 */

interface AdapterController <T> {
    fun onRefresh() { }
    fun onAdapterGetMoreItems(offset: Int) { }
    fun onAdapterItemClick(item: T) { }
    fun onAdapterItemClick(position: Int) { }
    fun onAdapterItemClick(item: T, imageView: ImageView) { }
}