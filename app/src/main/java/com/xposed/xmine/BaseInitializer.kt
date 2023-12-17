package com.xposed.xmine

import android.app.Application
import android.content.Context
import com.xposed.xmine.protocol.ProtocolRequest
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject

abstract class BaseInitializer {

    abstract val tag: String

    protected open var iRootInterface: IRootInterface? = null

    open fun init(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        Logger.e(tag, "BaseInitializer#init")
        val applicationName = loadPackageParam.appInfo.name
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
            bindRootService(baseContext)
            onAttachBaseContext(application, baseContext)
        }
    }

    fun bindRootService(baseContext: Context) {
        SuManager.bindService(baseContext) { rootAidl ->
            iRootInterface = rootAidl
            if (rootAidl != null) {
                onRootBindSuccess(baseContext)
            } else {
                onRootUnbind(baseContext)
            }
        }
    }

    protected open fun onAttachBaseContext(application: Application, baseContext: Context) {
        Logger.d(tag, "onAttachBaseContext application = %s", application)
    }

    protected open fun onRootBindSuccess(baseContext: Context) {
        Logger.d(tag, "onRootBindSuccess root = %s", iRootInterface)
    }

    protected open fun onRootUnbind(baseContext: Context) {
        Logger.d(tag, "onRootBindSuccess")
    }

    fun saveFile(fileName: String, content: String) {
        if (iRootInterface != null) {
            val json = JSONObject()
            json.put("filename", fileName)
            json.put("content", content)
            val response = iRootInterface?.handleSync(ProtocolRequest("save", json.toString()))
            Logger.d(tag, "response = %s", response)
        }
    }

    fun getFileContent(fileName: String): String? {
        if (iRootInterface != null) {
            val json = JSONObject()
            json.put("filename", fileName)
            val response = iRootInterface?.handleSync(ProtocolRequest("get", json.toString()))
            Logger.d(tag, "response = %s", response)
            return response?.data
        }
        return null
    }
}
