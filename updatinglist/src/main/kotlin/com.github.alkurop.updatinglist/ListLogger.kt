package com.github.alkurop.updatinglist

/**
 * Created by alkurop on 28.04.16.
 */
object ListLogger {
    var allowLogging = true
    fun log(tag: String, mes: String) {
        if (allowLogging) {
            log(tag, mes)
        }
    }
}