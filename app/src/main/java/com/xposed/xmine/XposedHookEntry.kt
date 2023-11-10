package com.xposed.xmine

import android.app.Application
import android.app.Instrumentation
import android.content.Context
import android.os.Bundle
import com.xposed.xmine.hooker.BaiduAd
import com.xposed.xmine.hooker.CsjAd
import com.xposed.xmine.hooker.DeJianHooker
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

    companion object {
//        init {
//            System.loadLibrary("dexkit")
//        }
    }

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        val packageName = lpparam?.packageName ?: return
        val processName = lpparam.processName
        val classLoader = lpparam.classLoader
        Logger.dd("Entry", "handleLoadPackage $packageName $processName $classLoader")
        if (XRuntime.inHooked) {
            return
        }
        XRuntime.packageName = packageName
        XRuntime.processName = processName
        XRuntime.classLoader = classLoader
        XRuntime.inHooked = true

        if (lpparam.appInfo.packageName == BuildConfig.APPLICATION_ID) {
            return
        }
        val appCls = lpparam.appInfo.className

        XposedHelpers.findAndHookMethod(
            Instrumentation::class.java,
            "onCreate",
            Bundle::class.java,
            newMethodBefore {
                val apkPath = lpparam.appInfo.sourceDir
                val applicationName = lpparam.appInfo.name
                Logger.dd("Entry", "attach Application")
                Logger.dd("Entry", "apkPath = $apkPath")
                Logger.dd("Entry", "applicationName = $applicationName")
                XRuntime.apkPath = apkPath

                runCatch {
                    XposedHelpers.findAndHookMethod(
                        classLoader.loadClass(applicationName),
                        "onCreate",
                        newMethodBefore {
                            Logger.dd("Entry", "attachBaseContext = $applicationName")
                            val context = it.thisObject as Context
                            Logger.dd("Entry", "非模块 attachBaseContext 读取模块文件数据")
//                            val auth = XMineContentProvider.AUTH
//
//                            val fileUri = Uri.parse("content://$auth/query")
//
//                            context.contentResolver.query(fileUri, null, null, null, null)?.use {
//                                Logger.d("Entry", "跨进程Query调用")
//                            }

//                            val result = context.contentResolver.call(fileUri, XMineContentProvider.READ_FILE_METHOD, "test.txt", null)
//
//                            if (result != null) {
//                                Logger.d("Entry", "跨进程call获取fd")
//                                val pfd = result.getParcelable<ParcelFileDescriptor>(XMineContentProvider.READ_FILE)
//                                Logger.d("Entry", "open > pfd = $pfd")
//                                ParcelFileDescriptor.AutoCloseInputStream(pfd).use { fin ->
//                                    FileOutputStream(File(context.filesDir, "copy_text.txt")).use { fout ->
//                                        fin.copyTo(fout)
//                                    }
//                                }
//                            }

//                            Logger.d("Entry", "拷贝文件内容 = %s", File(context.filesDir, "copy_text.txt").readText())

                            XRuntime.classLoader = context.classLoader
                            registerLifecycle(it.thisObject)
                            XRuntime.extClassLoader = it.thisObject.javaClass.classLoader
                            handleHook()
                        },
                    )
                }
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
//        val list = listOf<IHookReward>(CsjAd)
//        for (iHook in list) {
//            iHook.hookReward()
//        }
        DeJianHooker.init()
    }
}
