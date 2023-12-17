package com.xposed.xmine

import com.topjohnwu.superuser.Shell
import com.xposed.xmine.XRuntime.classLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.BuildConfig

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/16
 */
class XposedHookEntry : IXposedHookLoadPackage {

    companion object {

        const val TAG = "Entry"
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        val packageName = lpparam?.packageName ?: return
        val processName = lpparam.processName
        val classLoader = lpparam.classLoader
        Logger.d("Entry", "handleLoadPackage $packageName $processName $classLoader")
        if (!XRuntime.init(lpparam)) {
            Logger.d(TAG, "handleLoadPackage 初始化失败，已经初始化过")
            return
        }

        if (XRuntime.isModule) {
            Logger.d(TAG, "当前是Xposed模块化")
            return
        }
        AppInitializer.init(lpparam)
    }
}
