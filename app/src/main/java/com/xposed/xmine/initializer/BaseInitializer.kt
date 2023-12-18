package com.xposed.xmine.initializer

import android.app.Application
import android.content.Context
import android.os.Environment
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.internal.UiThreadHandler
import com.topjohnwu.superuser.nio.FileSystemManager
import com.xposed.xmine.IRootInterface
import com.xposed.xmine.SuManager
import com.xposed.xmine.XRuntime
import com.xposed.xmine.protocol.ProtocolRequest
import com.xposed.xmine.utils.Logger
import de.robv.android.xposed.XposedHelpers
import org.json.JSONObject

abstract class BaseInitializer<Init> {

    abstract val tag: String
    protected lateinit var application: Application
    protected var iRootInterface: IRootInterface? = null
    private var fileSystemManager: FileSystemManager? = null

    abstract fun init(param: Init)

    fun bindRootService(baseContext: Context) {
        SuManager.bindService(baseContext) { rootAidl ->
            Logger.d(tag, "bindRootService result = $rootAidl")
            iRootInterface = rootAidl
            if (rootAidl != null) {
                onRootBindSuccess(baseContext)
            } else {
                onRootUnbind(baseContext)
            }
        }
    }

    protected open fun onRootBindSuccess(baseContext: Context) {
        fileSystemManager = getFileManager()
        Logger.d(tag, "onRootBindSuccess $this root = %s fileSystemManager = %s", iRootInterface, fileSystemManager)
    }

    protected open fun onRootUnbind(baseContext: Context) {
        fileSystemManager = null
        Logger.d(tag, "onRootUnbind")
    }

    fun saveFileAsync(fileName: String, content: String) {
        Logger.d(tag, "saveFile  $this  $fileSystemManager filename = %s, content = %s", fileName, content)
        fileSystemManager?.let {
            Shell.EXECUTOR.execute {
                val docFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val suFile = it.getFile(docFile.absolutePath, "XMine/$fileName")
                if (suFile.parentFile?.exists() == false) {
                    suFile.parentFile?.mkdirs()
                }
                suFile.newOutputStream().write(content.toByteArray())
            }
        }
    }

    fun getFileAsync(fileName: String, result: (String) -> Unit) {
        Logger.d(tag, " $this getFileAsync $fileSystemManager filename = %s", fileName)
        fileSystemManager?.let {
            Shell.EXECUTOR.execute {
                val docFile = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)
                val suFile = it.getFile(docFile.absolutePath, "XMine/$fileName")
                if (!suFile.exists()) {
                    Logger.d(tag, "文件 %s 不存在", suFile.absolutePath)
                    return@execute
                }
                val content = suFile.newInputStream().readBytes()
                Logger.d(tag, "getFileAsync result = %s", String(content))
                UiThreadHandler.handler.post {
                    result(String(content))
                }
            }
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

    fun getFileManager(): FileSystemManager {
        Logger.d(tag, "getFileManager iRootInterface = %s", iRootInterface)
        val result = iRootInterface?.fileSystemService
        Logger.d(tag, "getFileManager result = %s", result)

        return if (result != null) {
            FileSystemManager.getRemote(result)
        } else {
            FileSystemManager.getLocal()
        }
    }

    fun forceGetApplication(): Application {
        if (!::application.isInitialized) {
            val aClass = XRuntime.loadClass("android.app.ActivityThread")
            val sCurrentActivityThread = XposedHelpers.getStaticObjectField(aClass, "sCurrentActivityThread")
            application = XposedHelpers.callMethod(sCurrentActivityThread, "mInitialApplication") as Application
        }
        return application
    }
}
