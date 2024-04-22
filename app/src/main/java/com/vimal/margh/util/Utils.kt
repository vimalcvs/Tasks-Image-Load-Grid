package com.vimal.margh.util

import android.util.Log


object Utils {

    fun getErrors(e: Exception?) {
        println("Errors:: " + Log.getStackTraceString(e))
    }
}