package com.github.alkurop.updatinglist

import android.os.Parcel
import android.os.Parcelable
import java.util.*

/**
 * Created by alkurop on 11.05.16.
 */
class AdapterStateModel(var canLoadMore: Boolean,
                        var lastAnimatedPosition: Int,
                        var progressCount: Int,
                        var currentPage: Int,
                        var scrollPosition: Int,
                        var isError: Boolean,
                        var isLoading: Boolean,
                        val items: MutableList<Parcelable>) : Parcelable {
    constructor(source: Parcel) : this(1.toByte().equals(source.readByte()), source.readInt(), source.readInt(),
              source.readInt(), source.readInt(), 1.toByte().equals(source.readByte()), 1.toByte().equals(source.readByte()), {
        val l = ArrayList<Parcelable>(); source.readList(l, Parcelable::class.java.classLoader); l
    }.invoke())


    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeByte((if (canLoadMore) 1 else 0).toByte())
        dest?.writeInt(lastAnimatedPosition)
        dest?.writeInt(progressCount)
        dest?.writeInt(currentPage)
        dest?.writeInt(scrollPosition)
        dest?.writeByte((if (isLoading) 1 else 0).toByte())
        dest?.writeByte((if (isError) 1 else 0).toByte())
        dest?.writeList(items)
    }

    companion object {
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