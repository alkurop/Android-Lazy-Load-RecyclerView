package com.github.alkurop.updatinglist.testclasses

import android.content.Context
import android.os.Parcelable
import com.github.alkurop.updatinglist.UpdatingListView

/**
 * Created by om on 7/3/16.
 */
open class TestList(context: Context) : UpdatingListView(context) {
    open fun testOnSaveInstanceState() = onSaveInstanceState()
    open fun testOnRestoreInstanceState(state: Parcelable) = onRestoreInstanceState(state)
}