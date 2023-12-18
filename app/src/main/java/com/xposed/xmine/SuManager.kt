package com.xposed.xmine

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import com.topjohnwu.superuser.Shell
import com.topjohnwu.superuser.ipc.RootService
import com.xposed.xmine.utils.Logger
import org.luckypray.dexkit.BuildConfig

object SuManager : Shell.Initializer() {

    private const val TAG = "SuManager"

    private lateinit var applicationContext: Context
    fun init() {
        Shell.enableVerboseLogging = true
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
                .setInitializers(ShellInitializer::class.java),
        )
    }

    fun bindService(context: Context, callback: (IRootInterface?) -> Unit) {
        Logger.d(TAG, "bindRootService")
        this.applicationContext = context
        val intent = Intent(context, CustomRootService::class.java)
        intent.addCategory(RootService.CATEGORY_DAEMON_MODE)
        RootService.bind(
            intent,
            object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    Logger.d(TAG, "onServiceConnected service = %s", service)
                    if (service == null) {
                        Logger.e(TAG, "bindRootService 失败，binder为空")
                        return
                    }
                    callback(IRootInterface.Stub.asInterface(service))
                }

                override fun onServiceDisconnected(name: ComponentName?) {
                    Log.e(TAG, "bindRootService 失败，onServiceDisconnected")
                    callback(null)
                }
            },
        )
    }

    fun isRoot(): Boolean {
        Logger.d(TAG, "isAppGrantedRoot = ${Shell.isAppGrantedRoot()}")
        val isRoot = Shell.getShell().isRoot
        Logger.d(TAG, "shell = $isRoot")
        return isRoot
    }

    class ShellInitializer : Shell.Initializer() {
        override fun onInit(context: Context, shell: Shell): Boolean {
            Logger.d("SuManager", "onInit shell = $shell")
            return true
        }
    }

    class RootConnection : ServiceConnection {

        var handleBind = false

        var bindState = false
            private set

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            Logger.d("SuManager", "连接到Root服务")
            bindState = true
            handleBind = false
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            Logger.d("SuManager", "Root服务断开")
            bindState = false
            handleBind = false
        }
    }
}
