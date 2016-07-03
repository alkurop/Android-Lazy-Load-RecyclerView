package com.github.alkurop.updatinglist

import android.support.v7.appcompat.BuildConfig
import android.view.View
import com.github.alkurop.updatinglist.testclasses.TestAdapter
import com.github.alkurop.updatinglist.testclasses.TestActivity
import com.github.alkurop.updatinglist.testclasses.TestList
import com.nhaarman.mockito_kotlin.spy
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.robolectric.Robolectric
import org.robolectric.RobolectricGradleTestRunner
import org.robolectric.annotation.Config

/**
 * Created by om on 7/3/16.
 */
@RunWith(RobolectricGradleTestRunner::class)
@Config(constants = BuildConfig::class, sdk = intArrayOf(21), packageName = "com.github.alkurop.updatinglist")
open class BaseTestClass {
    val roboActivity by lazy { Robolectric.buildActivity(TestActivity::class.java).create().start().resume() }
    val list by lazy { TestList(roboActivity.get()) }
    val listSpy by lazy { spy(list) }
    val adapter by lazy { TestAdapter() }
    val adapterSpy  by lazy { spy(adapter) }

    @Before
    fun init() {
        MockitoAnnotations.initMocks(this)
        list.setAdapter(adapterSpy)
        list.setEmptyView(View(roboActivity.get()))
        list.setProgressView(View(roboActivity.get()))
    }

    @Test fun stub() {
    }
}