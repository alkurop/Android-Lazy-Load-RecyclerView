package com.github.alkurop.updatinglist

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by alkurop on 11.05.16.
 */
open class AdapterStateModel(
          var canLoadMore: Boolean,
          var lastAnimatedPosition: Int,
          var progressCount: Int,
          var currentPage: Int,
          var isLoading: Boolean,
          val items: MutableList<out Any>) : Parcelable {
    open fun reset() {
        lastAnimatedPosition = 0
        canLoadMore = false
        currentPage = 0
        isLoading = false
        items.clear()
        progressCount = 0
    }

    constructor(source: Parcel) : this(1.equals(source.readInt()), source.readInt(), source.readInt(), source.readInt
    (), 1.equals(source.readInt()), ArrayList<Any>())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt((if (canLoadMore) 1 else 0))
        dest?.writeInt(lastAnimatedPosition)
        dest?.writeInt(progressCount)
        dest?.writeInt(currentPage)
        dest?.writeInt((if (isLoading) 1 else 0))
    }

    companion object {
        fun getDefaultInstance() = AdapterStateModel  (false, 0, 0, 0, false, ArrayList<Any>())

        @JvmField final val CREATOR: Parcelable.Creator<AdapterStateModel> = object : Parcelable.Creator<AdapterStateModel> {
            override fun createFromParcel(source: Parcel): AdapterStateModel {
                return AdapterStateModel(source)
            }

            override fun newArray(size: Int): Array<AdapterStateModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}