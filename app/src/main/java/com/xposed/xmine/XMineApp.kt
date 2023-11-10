package com.xposed.xmine

import android.app.Application
import android.content.Context

/**
 *
 * @author  zhangzhipeng
 * @date    2023/11/6
 */
class XMineApp: Application() {

    override fun attachBaseContext(base: Context?) {
        Logger.dd("XMineApp", "attachBaseContext")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        Logger.dd("XMineApp", "onCreate")
        super.onCreate()
    }
}