package com.github.alkurop.updatinglist

import android.os.Parcel
import android.os.Parcelable
import android.view.View

/**
 * Created by alkurop on 11.05.16.
 */
class UpdatingListSaveState : View.BaseSavedState {
    lateinit var adapterState: AdapterStateModel

    constructor(source: Parcel) : super(source) {
        adapterState = source.readParcelable<AdapterStateModel>(AdapterStateModel::class.java.classLoader)
    }

    constructor(superState: Parcelable, model: AdapterStateModel) : super(superState) {
        this.adapterState = model
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        dest?.writeParcelable(adapterState, 0)
    }

    companion object {
        @JvmField final val CREATOR: Parcelable.Creator<UpdatingListSaveState> = object : Parcelable.Creator<UpdatingListSaveState> {
            override fun createFromParcel(source: Parcel): UpdatingListSaveState {
                return UpdatingListSaveState(source)
            }

            override fun newArray(size: Int): Array<UpdatingListSaveState?> {
                return arrayOfNulls(size)
            }
        }
    }
}