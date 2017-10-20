package com.alkurop.updatingexample

import android.view.LayoutInflater
import android.view.ViewGroup
import com.github.alkurop.updatinglist.BaseLoadMoreAdapter
import com.github.alkurop.updatinglist.BaseViewHolder

/**
 * Created by alkurop on 30.06.16.
 */
class ExampleAdapter() : BaseLoadMoreAdapter<ILongProcessData>() {
    val SOME_OTHER_VIEW_TYPE = 1
    override fun onCreateProgressVH(viewGroup: ViewGroup): BaseViewHolder<ILongProcessData> {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.default_item_loading_view, viewGroup, false)
        return ProgressVH(view)
    }

    override fun onCreateVH(viewGroup: ViewGroup, viewType: Int): BaseViewHolder<ILongProcessData> {
        val vh: BaseViewHolder<ILongProcessData>
        if (viewType == SOME_OTHER_VIEW_TYPE) {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.example_item_picture_view, viewGroup, false)
            vh = PictureExampleViewHolder(view)
        } else {
            val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.example_item_int_view, viewGroup, false)
            vh = ExampleViewHolder(view)
        }
        return vh
    }

    override fun getItemViewType(position: Int): Int {
        if (getItem(position) is LongProcessImageData)
            return SOME_OTHER_VIEW_TYPE

        return super.getItemViewType(position)
    }
}

