package com.xposed.xmine.hooker.base

import com.xposed.xmine.utils.Logger

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
interface ILogMixin {
    val TAG: String

    fun i(msg: String, vararg varargs: Any?) {
        Logger.i(TAG, msg, *varargs)
    }

    fun d(msg: String, vararg varargs: Any?) {
        Logger.d(TAG, msg, *varargs)
    }

    fun e(msg: String, vararg varargs: Any?) {
        Logger.d(TAG, msg, *varargs)
    }
}
