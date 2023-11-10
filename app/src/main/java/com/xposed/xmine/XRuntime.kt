package com.xposed.xmine

import android.os.Handler
import android.os.Looper
import org.luckypray.dexkit.DexKitBridge

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object XRuntime {

    const val pluginPackage = "com.xposed.xmine"

    var apkPath: String = ""

    var classLoader: ClassLoader = ClassLoader.getSystemClassLoader()
        internal set

    var extClassLoader: ClassLoader? = null

    var packageName: String = ""
        internal set

    var processName: String = ""
        internal set

    var inHooked = false
        internal set

    val isModule: Boolean get() = packageName == BuildConfig.APPLICATION_ID

    val mainHandler: Handler by lazy {
        Handler(Looper.getMainLooper())
    }

    /**
     * 本方法需要可能返回空，但强制返回空值
     */
    fun loadClass(name: String): Class<*> {
        val res = runCatch {
            classLoader.loadClass(name)
        }.getOrElse { extClassLoader?.loadClass(name) }

        // throw null point
        return res!!
    }

    fun loadDexKit() {

    }
}
