package com.github.alkurop.updatinglist

import android.support.v7.appcompat.BuildConfig
import com.github.alkurop.updatinglist.testclasses.DataObject
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.spy
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

/**
 * Created by alkurop on 01.07.16.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21))
class BaseLoadMoreAdapterTest : BaseTestClass() {
    @Test
    fun testClear() {
        val spyState = spy(adapterSpy.state)
        adapterSpy.state = spyState
        adapterSpy.clear()
        verify(spyState, times(1)).reset()
    }

    @Test
    fun testAddItems() {
        val oldPage = adapter.state.currentPage
        val data = DataObject()
        adapterSpy.addItems(listOf(data))
        verify(adapterSpy, times(1)).setLoadingMore(false)
        assert(adapter.state.currentPage - oldPage == 1)
        assert(adapter.state.items.contains(data))
    }

    @Test
    fun testSetCanLoadMore() {
        val canLoadMore = true
        adapterSpy.setCanLoadMore(canLoadMore)
        assert(adapterSpy.state.canLoadMore)
    }

    @Test
    fun testAddItem() {
        val rrr = Math.abs(Math.random() * 1000).toInt()
        for (i in 1..rrr) {
            adapterSpy.addItem(any())
        }
        verify(adapterSpy, times(rrr)).setLoadingMore(false)
        verify(adapterSpy, times(rrr)).addItemToPosition(any(), 0)
    }

    @Test
    fun testAddItemToPosition() {
        val data = DataObject()
        val rrr = Math.abs(Math.random() * 1000).toInt()
        for (i in 1..rrr) {
            adapterSpy.addItemToPosition(data, 0)
        }
        verify(adapterSpy, times(rrr)).setLoadingMore(false)
        assert(adapter.state.items.contains(data))
    }

    @Test
    fun testShowLoadMoreEmpty() {
        adapterSpy.clear()
        adapterSpy.state.isLoading = false
        adapterSpy.state.progressCount = 0
        adapterSpy.showLoadMore()
        assert(adapterSpy.state.isLoading == false)
        assert(adapterSpy.state.progressCount == 0)
    }

    @Test
    fun testShowLoadMoreNotEmpty() {
        adapterSpy.addItem(any())
        adapterSpy.state.isLoading = false
        adapterSpy.state.progressCount = 0
        adapterSpy.showLoadMore()
        assert(adapterSpy.state.isLoading == true)
        assert(adapterSpy.state.progressCount == 1)
    }

    @Test
    fun testHideLoadMore() {
        adapterSpy.state.progressCount = 1
        adapterSpy.state.isLoading = true
        adapterSpy.hideLoadMore()
        assert(adapterSpy.state.progressCount == 0)
        assert(adapterSpy.state.isLoading == false)
    }

    @Test
    fun testLoadFromModel() {
        val rvSpy = spy(list.recycler)
        val state = AdapterStateModel.getDefaultInstance()
        adapterSpy.onAttachedToRecyclerView(rvSpy)

        adapterSpy.loadFromModel(state)

        assert(adapterSpy.state == state)
    }
}