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
    val mAdapter: ExampleAdapter by lazy { ExampleAdapter () }
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
        list.setLoadingViews(
                  R.layout.example_native_loading_view,
                  R.layout.example_empty_view)

        list.setAdapter(mAdapter)
        list.setLoadMoreListener  {
            mSubscriptions.forEach { it.unsubscribe() }
            mSubscriptions.clear()
            startLoadingOperation(offset = it)
        }
        list.setSwipeRefreshListener {
            Log.d("loadingOperation", "loadingOperation")
            mAdapter.clear()
            startLoadingOperation(offset = 0)
        }
    }

    fun startLoadingOperation(offset: Int) {
        list.showLoading(true)
        val sub = LongProcessMock.getLoadObservable(offset)
                  .subscribeOn(Schedulers.io())
                  .observeOn(AndroidSchedulers.mainThread())
                  .doOnTerminate {
                      list.showLoading(false)
                  }
                  .subscribe ({ processResult(it) }, {
                      list.onError()
                      it.printStackTrace()
                  })
        mSubscriptions.add(sub)
    }

    fun processResult(response: LongResponse) {
        val canLoadMore = (response.maxCount - response.data.size - response.offset) > 0
        mAdapter.addItems(response.data)
        mAdapter.setCanLoadMore(canLoadMore)
    }

    override fun onStop() {
        list.onStop()
        mSubscriptions.forEach { it.unsubscribe() }
        mSubscriptions.clear()
        super.onStop()
    }
}