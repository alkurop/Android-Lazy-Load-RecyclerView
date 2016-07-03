package com.github.alkurop.updatinglist

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import android.support.v7.appcompat.BuildConfig
import android.view.View
import com.github.alkurop.updatinglist.mock_classes.Adapter
import com.github.alkurop.updatinglist.mock_classes.TestActivity
import com.nhaarman.mockito_kotlin.MockitoKotlin
import com.nhaarman.mockito_kotlin.any
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.*
import org.mockito.Mock
import org.mockito.Mockito


/**
 * Created by alkurop on 01.07.16.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21), packageName = "com.github.alkurop.updatinglist")
open class UpdatingListViewTest {
    val roboActivity by lazy { Robolectric.buildActivity(TestActivity::class.java).create().start().resume() }
    val list by lazy { TestList(roboActivity.get()) }
    val listSpy by lazy { spy(list) }
    val adapter by lazy { Adapter() }
    val adapterSpy  by lazy { spy(adapter) }

    init {
        MockitoKotlin.registerInstanceCreator<AdapterStateModel> { AdapterStateModel(false, 0, 0, 0, 0, false, mutableListOf()) }

    }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)

        list.setAdapter(adapterSpy)
        list.setEmptyView(View(roboActivity.get()))
        list.setProgressView(View(roboActivity.get()))
    }

    @Test
    @Throws
    fun testOnStop() {
        listSpy.onStop()
        verify(listSpy, times(1)).hideLoading()
        verify(adapterSpy, times(1)).setLoadingMore(false)
    }

    @Test
    @Throws
    fun testHideLoading() {
        listSpy.hideLoading()
        assert(listSpy.swipeView.isRefreshing == false)
        assert(listSpy.getProgressView()?.visibility == View.GONE)
        assert(listSpy.getEmptyView()?.visibility == View.VISIBLE)
    }

    @Test
    @Throws
    fun testShowLoading() {
        listSpy.showLoading()
        assert(listSpy.getProgressView()?.visibility == View.VISIBLE)
        assert(listSpy.getEmptyView()?.visibility == View.GONE)
    }

    @Test
    @Throws
    fun testOnError() {
        listSpy.onError()
        verify(listSpy, times(1)).hideLoading()
        verify(adapterSpy, times(1)).setLoadingMore(false)
    }

    @Test
    @Throws
    fun testOnSaveInstanceState() {
        val state = listSpy.testOnSaveInstanceState()
        val b = state as Bundle
        assertNotNull(b.getParcelable<AdapterStateModel>("adapterModel"))
        assertNotNull(b.getParcelable<Parcelable>("superState"))
        assertNotNull(b.getSparseParcelableArray<Parcelable>(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY))
    }

    @Test
    @Throws
    fun testOnRestoreInstanceState() {
        val state = listSpy.testOnSaveInstanceState()
        val b = state as Bundle
        list.testOnRestoreInstanceState(b)
        verify(adapterSpy, times(1)).loadFromModel(any())
    }

    open class TestList(context: Context) : UpdatingListView(context) {
        open fun testOnSaveInstanceState() = onSaveInstanceState()
        open fun testOnRestoreInstanceState(state: Parcelable) = onRestoreInstanceState(state)
    }
}