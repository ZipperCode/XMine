package com.xposed.xmine.initializer

import android.app.Application
import android.content.Context
import com.xposed.xmine.utils.Logger
import com.xposed.xmine.XRuntime
import com.xposed.xmine.utils.findAndHookMethodAfter
import com.xposed.xmine.utils.findAndHookMethodBefore
import de.robv.android.xposed.callbacks.XC_LoadPackage

object AppInitializer : BaseInitializer<XC_LoadPackage.LoadPackageParam>() {

    override val tag: String get() = "AppInitializer"

    override fun init(param: XC_LoadPackage.LoadPackageParam) {
        Logger.e(tag, "BaseInitializer#init")
        val applicationName = param.appInfo.name
        val applicationCls = XRuntime.classLoader.loadClass(applicationName)
        if (applicationCls == null) {
            Logger.d(tag, "applicationClass is null")
            return
        }

        findAndHookMethodBefore(applicationCls, "attachBaseContext", Context::class.java) {
            Logger.d(tag, "attachBaseContext")
            val baseContext = it.args[0] as Context
            val application = it.thisObject as Application
            XRuntime.extClassLoader = it.thisObject.javaClass.classLoader
            onAttachBaseContext(application, baseContext)
        }

        findAndHookMethodAfter(applicationCls, "onCreate") {
            Logger.d(tag, "onCreate")
            application = it.thisObject as Application
            bindRootService(application.baseContext)
        }
    }

    private fun onAttachBaseContext(application: Application, baseContext: Context) {
        Logger.d(tag, "onAttachBaseContext application = %s", application)
    }

    override fun onRootBindSuccess(baseContext: Context) {
        super.onRootBindSuccess(baseContext)

        getFileAsync("test.txt"){

        }
    }

    override fun onRootUnbind(baseContext: Context) {
        super.onRootUnbind(baseContext)
    }
}
