package com.github.alkurop.updatinglist

import android.support.v7.app.AppCompatActivity
import android.support.v7.appcompat.BuildConfig
import com.github.alkurop.updatinglist.mock_classes.DataObject
import com.github.alkurop.updatinglist.mock_classes.TestActivity
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.Spy
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

/**
 * Created by alkurop on 01.07.16.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21), packageName = "com.github.alkurop.updatinglist")
open class UpdatingListViewTest {
    lateinit var activity: AppCompatActivity

    lateinit var list: UpdatingListView
    lateinit var listSpy: UpdatingListView
    @Mock
    lateinit var adapter: BaseLoadMoreAdapter<DataObject>
    val adapterState =   AdapterStateModel(false, 0, 0, 0, 0, false, mutableListOf())

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        activity = Robolectric.buildActivity(TestActivity::class.java).create().start().resume().get()
        Mockito.`when`(adapter.state).thenReturn(adapterState)
        list = UpdatingListView(activity)
        list.setAdapter(adapter)
        listSpy = spy(list)
    }

    @Test
    @Throws
    fun testOnStop() {
        list.onStop()
        verify(listSpy, times(1)).hideLoading()
        verify(adapter, times(1)).setLoadingMore(false)
    }

    @Throws
    fun testShowLoading() {
    }

    @Throws
    fun testHideLoading() {
    }

    @Throws
    fun testOnError() {
    }

    @Throws
    fun testOnSaveInstanceState() {
    }

    @Throws
    fun testOnRestoreInstanceState() {
    }
}