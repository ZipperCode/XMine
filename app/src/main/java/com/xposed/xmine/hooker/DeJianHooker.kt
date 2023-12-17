package com.xposed.xmine.hooker

import com.xposed.xmine.Logger
import com.xposed.xmine.XRuntime
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers

object DeJianHooker {

    fun init() {
        if (XRuntime.packageName != "com.chaozh.iReader.dj") {
            return
        }
        Logger.d("initHook#package= %s", XRuntime.packageName)
        hookRealTime()
        hookVipTime()
    }

    private fun hookRealTime() {
        val cls = XRuntime.classLoader.loadClass("com.zhangyue.iReader.plugin.PluginRely")
        XposedHelpers.findAndHookMethod(
            cls,
            "isDebuggable",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    param?.result = true
                }
            },
        )

        XposedHelpers.findAndHookMethod(
            cls,
            "getReadTime",
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val value = param?.result as Int? ?: (120 * 60)
                    param?.result = value * 2
                }
            },
        )
    }

    private fun hookVipTime() {
        val spClass = XRuntime.classLoader.loadClass("com.zhangyue.iReader.DB.SPHelperTemp")
        val getLongMethod = spClass.getDeclaredMethod("getLong", String::class.java, Long::class.java)
        XposedBridge.hookMethod(
            getLongMethod,
            object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam?) {
                    super.afterHookedMethod(param)
                    val key = param?.args?.get(0)
                    Logger.i("TimeCall#Sp getLong key = %s， value = %s", key, param?.result)
                    if (key == "video_vip_time") {
                        param.result = 1200L
                    }
                }
            },
        )
    }
}
