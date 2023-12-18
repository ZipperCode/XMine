package com.xposed.xmine

import com.xposed.xmine.initializer.AppInitializer
import com.xposed.xmine.initializer.ModuleInitializer
import com.xposed.xmine.utils.Logger
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/16
 */
class XposedHookEntry : IXposedHookZygoteInit, IXposedHookLoadPackage {

    companion object {

        const val TAG = "Entry"

        init {
            SuManager.init()
        }
    }

    override fun initZygote(startupParam: IXposedHookZygoteInit.StartupParam?) {
        Logger.d("Entry", "initZygote")

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
            XposedHelpers.findAndHookMethod(XRuntime.loadClass(ModuleInitializer::class.java.name), "isActive", XC_MethodReplacement.returnConstant(true))
            return
        }
        AppInitializer.init(lpparam)
    }
}
