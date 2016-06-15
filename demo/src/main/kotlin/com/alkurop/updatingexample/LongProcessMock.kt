package com.alkurop.updatingexample

import android.os.Parcel
import android.os.Parcelable
import rx.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by alkurop on 27.04.16.
 */
object LongProcessMock {
    val maxItems = 100
    val pageSize = 20
    val seconds = 3L
    fun getLoadObservable(offset: Int) =
              Observable.create<LongResponse> {
                  try {
                      if (!it.isUnsubscribed) {
                          val r = LongProcessMock.load(offset)
                          it.onNext(r)
                          it.onCompleted();
                      }
                  } catch (e: Exception) {
                      it.onError(e);
                  }
              }

    private fun load(offset: Int): LongResponse {
        TimeUnit.SECONDS.sleep(seconds)
        var count = maxItems - offset
        if (count > pageSize)
            count = pageSize
        if (count < 0)
            count == 0
        val dataList = mutableListOf<ILongProcessData>()
        for (i in 1..count) {
            if (i % 3 == 0) {
                dataList.add(LongProcessImageData("https://pp.vk.me/c633920/v633920030/e427/_rt6urzI2Wg.jpg"))
            } else {
                dataList.add(LongProcessIntData(offset + i - 1))
            }
        }
        return LongResponse(offset, dataList, maxItems)
    }
}

data class LongResponse(val offset: Int, val data: List<ILongProcessData>, val maxCount: Int)

interface ILongProcessData : Parcelable
data class LongProcessImageData(val imagePath: String) : ILongProcessData {
    constructor(source: Parcel) : this(source.readString())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeString(imagePath)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<LongProcessImageData> = object : Parcelable.Creator<LongProcessImageData> {
            override fun createFromParcel(source: Parcel): LongProcessImageData {
                return LongProcessImageData(source)
            }

            override fun newArray(size: Int): Array<LongProcessImageData?> {
                return arrayOfNulls(size)
            }
        }
    }
}

data class LongProcessIntData(val value: Int) : ILongProcessData {
    constructor(source: Parcel) : this(source.readInt())

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeInt(value)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<LongProcessIntData> = object : Parcelable.Creator<LongProcessIntData> {
            override fun createFromParcel(source: Parcel): LongProcessIntData {
                return LongProcessIntData(source)
            }

            override fun newArray(size: Int): Array<LongProcessIntData?> {
                return arrayOfNulls(size)
            }
        }
    }
}