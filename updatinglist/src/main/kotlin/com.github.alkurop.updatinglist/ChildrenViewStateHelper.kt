package com.github.alkurop.updatinglist

import android.os.Bundle
import android.os.Parcelable
import android.util.SparseArray
import android.view.ViewGroup

/**
 * Created by alkurop on 30.06.16.
 */
class ChildrenViewStateHelper (val viewGroup: ViewGroup) {


    fun saveChildrenState(): SparseArray<Parcelable> {
        val array = SparseArray<Parcelable>()
        for (i in 0..viewGroup!!.childCount - 1) {
            val bundle = Bundle()
            val childArray = SparseArray<Parcelable>() //create independent SparseArray for each child (View or ViewGroup)
            viewGroup!!.getChildAt(i).saveHierarchyState(childArray)
            bundle.putSparseParcelableArray(DEFAULT_CHILDREN_STATE_KEY, childArray)
            array.append(i, bundle)
        }
        return array
    }


    fun restoreChildrenState(childrenState: SparseArray<Parcelable>?) {
        if (null == childrenState) {
            return
        }
        for (i in 0..viewGroup!!.childCount - 1) {
            val bundle = childrenState.get(i) as Bundle
            val childState = bundle.getSparseParcelableArray<Parcelable>(DEFAULT_CHILDREN_STATE_KEY)
            viewGroup!!.getChildAt(i).restoreHierarchyState(childState)
        }
    }

    companion object {
        val DEFAULT_CHILDREN_STATE_KEY = ChildrenViewStateHelper::class.java.simpleName + ".childrenState"
    }
}