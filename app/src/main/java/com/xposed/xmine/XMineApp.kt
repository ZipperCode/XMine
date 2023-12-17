package com.xposed.xmine

import android.app.Application
import android.content.Context
import android.os.Debug

/**
 *
 * @author zhangzhipeng
 * @date 2023/11/6
 */
class XMineApp : Application() {

    override fun attachBaseContext(base: Context) {
        Logger.d("XMineApp", "attachBaseContext")
        super.attachBaseContext(base)
    }

    override fun onCreate() {
        Logger.d("XMineApp", "onCreate")
        super.onCreate()
        ModuleInitializer.bindRootService(applicationContext)
    }
}
