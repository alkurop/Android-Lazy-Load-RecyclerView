package com.github.alkurop.updatinglist

import android.os.Bundle
import android.os.Parcelable
import android.view.View
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.times
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.mockito.Mockito.verify


/**
 * Created by alkurop on 01.07.16.
 */
open class UpdatingListViewTest : BaseTestClass() {

    @Test
    fun testOnStop() {
        adapterSpy.state.isLoading = true
        listSpy.stopLoading()

        verify(listSpy, times(1)).hideLoading()

        verify(adapterSpy, times(1)).setLoadingMore(false)
    }

    @Test
    fun testHideLoading() {
        listSpy.hideLoading()

        assert(listSpy.swipeView.isRefreshing == false)
        assert(listSpy.getProgressView()?.visibility == View.GONE)
        assert(listSpy.getEmptyView()?.visibility == View.VISIBLE)
    }

    @Test
    fun testShowLoading() {
        listSpy.showLoading()

        assert(listSpy.getProgressView()?.visibility == View.VISIBLE)
        assert(listSpy.getEmptyView()?.visibility == View.GONE)
    }

    @Test
    fun testOnError() {
        listSpy.onError()

        verify(listSpy, times(1)).hideLoading()
        verify(adapterSpy, times(1)).setLoadingMore(false)
    }

    @Test
    fun testOnSaveInstanceState() {
        val state = listSpy.testOnSaveInstanceState()
        val b = state as Bundle

        assertNotNull(b.getParcelable<AdapterStateModel>("adapterModel"))
        assertNotNull(b.getParcelable<Parcelable>("superState"))
        assertNotNull(b.getSparseParcelableArray<Parcelable>(ChildrenViewStateHelper.DEFAULT_CHILDREN_STATE_KEY))
    }

    @Test
    fun testOnRestoreInstanceState() {
        val state = listSpy.testOnSaveInstanceState()
        val b = state as Bundle
        list.testOnRestoreInstanceState(b)

        verify(adapterSpy, times(1)).loadFromModel(any())
    }
}