package com.xmq.codeline

import android.util.Log

/**
 * @author xmqyeah
 * @CreateDate 2021/11/24 21:52
 */
object TestUtil {
    fun onCreate() {
        Log.d("TestUtil", "onCreate()")
        MockLog.i("TestUtil", "onCreate()")
    }

    fun clickLog() {
        Log.d("TestUtil", "clickLog()")
        MockLog.i("TestUtil", "clickLog()")
    }
}