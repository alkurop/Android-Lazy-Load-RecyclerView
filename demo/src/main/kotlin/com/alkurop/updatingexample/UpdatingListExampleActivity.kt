package com.alkurop.updatingexample

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.github.alkurop.updatinglist.BaseLoadMoreAdapter
import com.github.alkurop.updatinglist.BaseViewHolder
import com.github.alkurop.updatinglist.UpdatingListView
import com.squareup.picasso.Picasso

import kotlinx.android.synthetic.main.example_activity_main.*
import kotlinx.android.synthetic.main.example_item_int_view.view.*
import kotlinx.android.synthetic.main.example_item_picture_view.view.*
import rx.Subscription
import rx.android.schedulers.AndroidSchedulers
import rx.schedulers.Schedulers

/**
 * Created by alkurop on 26.04.16.
 */
class UpdatingListExampleActivity : AppCompatActivity() {

    val mList: UpdatingListView by lazy { UpdatingListView(this) }
    private val mAdapter: ExampleAdapter by lazy { ExampleAdapter () }
    val mSubscriptions: MutableList<Subscription> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.example_activity_main)
        initUpdatingList()
        if (savedInstanceState == null) {
            startLoadingOperation(offset = 0)
        }
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle?) {
        super.onRestoreInstanceState(savedInstanceState)
        if (savedInstanceState != null) {
            if (mAdapter.getItemsSize() == 0) {
                startLoadingOperation(offset = 0)
            }
        }
    }

    fun initUpdatingList() {
        container.addView(mList)
        mList.setLoadingViews(
                  R.layout.example_native_loading_view,
                  R.layout.example_empty_view)

        mList.setAdapter(mAdapter)
        mList.loadMoreListener = {
            mSubscriptions.forEach { it.unsubscribe() }
            mSubscriptions.clear()
            startLoadingOperation(offset = it)
        }
        mList.swipeRefreshListener = {
            Log.d("loadingOperation", "loadingOperation")
            mAdapter.clear()
            startLoadingOperation(offset = 0)
        }
    }

    fun startLoadingOperation(offset: Int) {
        mList.isLoading = true
        val sub = LongProcessMock.getLoadObservable(offset)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnTerminate {
                      Log.d("loadingOperation", "finish")
                      mList.isLoading = false
                  }
                  .subscribe ({ processResult(it) }, {
                      mList.onError()
                      it.printStackTrace()
                  })
        mSubscriptions.add(sub)
    }

    fun processResult(response: LongResponse) {
        val canLoadMore = (response.maxCount - response.data.size - response.offset) > 0
        mAdapter.addItems(response.data )
        mAdapter.setCanLoadMore(canLoadMore)
    }

    override fun onStop() {
        mList.onStop()
        mSubscriptions.forEach { it.unsubscribe() }
        mSubscriptions.clear()
        super.onStop()
    }
}

private class ExampleAdapter() : BaseLoadMoreAdapter<ILongProcessData>() {
    val SOME_OTHER_VIEW_TYPE = 1
    override fun onCreateProgressVH(viewGroup: ViewGroup): BaseViewHolder<ILongProcessData> {
        val view = LayoutInflater.from(viewGroup.context).inflate(R.layout.example_item_loading_view, viewGroup, false)
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



