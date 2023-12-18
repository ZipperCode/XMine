package com.xposed.xmine.hooker

import com.xposed.xmine.utils.Logger
import com.xposed.xmine.utils.findAndHookMethodBefore
import com.xposed.xmine.utils.runCatch
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.net.InetSocketAddress
import java.net.Proxy

object ProxyHooker {

    private const val TAG = "ProxyHooker"

    fun hook(loadPackageParam: LoadPackageParam) {
        runCatch {
            Logger.d(TAG, "hook")
            val urlClazz = XposedHelpers.findClass("java.net.URL", loadPackageParam.classLoader)
            findAndHookMethodBefore(urlClazz, "openConnection", Proxy::class.java) {
                val proxy = it.args[0] as Proxy
                if (proxy == Proxy.NO_PROXY) {
                    Logger.d(TAG, "URL#openConnection 应用添加了NO_PROXY")
                    val proxyHost = System.getProperty("http.proxyHost") ?: ""
                    val proxyPort = System.getProperty("http.proxyPort") ?: ""
                    Logger.d(TAG, "获取系统代理 %s:%s", proxyHost, proxyPort)
                    if (proxyHost.isNotBlank() && proxyPort.isNotBlank()) {
                        val port = proxyPort.toInt()
                        it.args[0] = Proxy(Proxy.Type.DIRECT, InetSocketAddress(proxyHost, port))
                    } else {
                        it.args[0] = Proxy.NO_PROXY
                    }
                }
            }
        }

        runCatch {
            val clazz = XposedHelpers.findClass("okhttp3.OkHttpClient.Builder", loadPackageParam.classLoader)

            findAndHookMethodBefore(clazz, "proxy", Proxy::class.java) {
                val proxy = it.args[0] as Proxy
                if (proxy == Proxy.NO_PROXY) {
                    Logger.d(TAG, "okhttp#proxy 应用添加了NO_PROXY")
                    val proxyHost = System.getProperty("http.proxyHost") ?: ""
                    val proxyPort = System.getProperty("http.proxyPort") ?: ""
                    Logger.d(TAG, "获取系统代理 %s:%s", proxyHost, proxyPort)
                    if (proxyHost.isNotBlank() && proxyPort.isNotBlank()) {
                        val port = proxyPort.toInt()
                        it.args[0] = Proxy(Proxy.Type.DIRECT, InetSocketAddress(proxyHost, port))
                    } else {
                        it.args[0] = Proxy.NO_PROXY
                    }
                }
            }
        }
    }
}
