package com.alkurop.updatingexample

import android.view.View
import com.github.alkurop.updatinglist.BaseViewHolder
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.example_item_int_view.view.*
import kotlinx.android.synthetic.main.example_item_picture_view.view.*

/**
 * Created by alkurop on 30.06.16.
 */
class ExampleViewHolder(itemView: View) : BaseViewHolder<ILongProcessData>(itemView) {
    override fun bind(data: ILongProcessData) {
        super.bind(data)
        itemView.mTextView.text = "item number ${(data as LongProcessIntData).value}"
    }
}

class PictureExampleViewHolder(itemView: View) : BaseViewHolder<ILongProcessData>(itemView) {
    override fun bind(data: ILongProcessData) {
        super.bind(data)
        Picasso.with(itemView.context).load((data as LongProcessImageData).imagePath).into(itemView.mImageView)
    }
}
