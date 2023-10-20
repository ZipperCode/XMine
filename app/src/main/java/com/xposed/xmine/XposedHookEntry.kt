package com.xposed.xmine

import android.app.Application
import com.xposed.xmine.hooker.BaiduAd
import com.xposed.xmine.hooker.CsjAd
import com.xposed.xmine.hooker.GdtAd
import com.xposed.xmine.hooker.KsAd
import com.xposed.xmine.hooker.TanxAd
import com.xposed.xmine.hooker.base.IHookReward
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/16
 */
class XposedHookEntry : IXposedHookLoadPackage {
    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName ?: return
        val processName = lpparam.processName
        val classLoader = lpparam.classLoader
        if (XRuntime.inHooked) {
            return
        }
        XRuntime.packageName = packageName
        XRuntime.processName = processName
        XRuntime.classLoader = classLoader
        XRuntime.inHooked = true

        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            newMethodBefore {
                registerLifecycle(it.thisObject)
                XRuntime.extClassLoader = it.thisObject.javaClass.classLoader
                handleHook()
            },
        )
    }

    private fun registerLifecycle(any: Any) {
        if (any is Application) {
            any.unregisterActivityLifecycleCallbacks(ActivityStackManager)
            any.registerActivityLifecycleCallbacks(ActivityStackManager)
        }
    }

    private fun handleHook() {
        val list = listOf<IHookReward>(BaiduAd, CsjAd, GdtAd, KsAd, TanxAd)
        for (iHook in list) {
            iHook.hookReward()
        }
    }
}
