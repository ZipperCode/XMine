package com.xposed.xmine

import android.util.Log

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object Logger {

    private const val MAIN_TAG = "XMine"

    fun i(msg: String, vararg varargs: Any?) {
        Log.i(MAIN_TAG, msg.format(*varargs))
    }

    fun i(tag: String, msg: String, vararg varargs: Any?) {
        Log.i(MAIN_TAG, "[$tag]: $msg".format(*varargs))
    }

    fun d(msg: String, vararg varargs: Any?) {
        Log.d(MAIN_TAG, msg.format(*varargs))
    }

    fun d(tag: String, msg: String, vararg varargs: Any?) {
        Log.d(MAIN_TAG, "[$tag]: $msg".format(*varargs))
    }

    fun e(msg: String, vararg varargs: Any?) {
        Log.e(MAIN_TAG, msg.format(*varargs))
    }

    fun e(tag: String, msg: String, vararg varargs: Any?) {
        Log.e(MAIN_TAG, "[$tag]: $msg".format(*varargs))
    }

    fun printStackTrace(e: Throwable? = null) {
        Log.e(MAIN_TAG, Log.getStackTraceString(e ?: Throwable()))
    }
}
