package com.alkurop.updatinglist

/**
 * Created by alkurop on 28.04.16.
 */
internal object ListLogger {
    val allowLogging = true
    fun log(tag: String, mes: String) {
        if (allowLogging) {
            log(tag, mes)
        }
    }
}