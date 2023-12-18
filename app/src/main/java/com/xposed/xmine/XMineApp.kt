package com.xposed.xmine

import android.app.Application
import android.content.Context
import com.xposed.xmine.initializer.ModuleInitializer
import com.xposed.xmine.utils.Logger

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
        super.onCreate()
        ModuleInitializer.init(this)
        Logger.d("XMineApp", "onCreate isActive = %s", ModuleInitializer.isActive())
    }
}
