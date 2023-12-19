package com.xposed.xmine.third

import com.xposed.xmine.utils.Logger
import com.xposed.xmine.utils.findAndHookMethodAfter
import com.xposed.xmine.utils.findAndHookMethodBefore
import com.xposed.xmine.utils.runCatch
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.security.SecureRandom
import javax.net.ssl.KeyManager
import javax.net.ssl.SSLSocketFactory
import javax.net.ssl.TrustManager

object SslHooker {

    const val TAG = "RePinningHooker"

    fun hook(loadPackageParam: LoadPackageParam) = runCatch {
        Logger.d(TAG, "[.] Cert Pinning Bypass/Re-Pinning")
        val classLoader = loadPackageParam.classLoader

        findAndHookMethodAfter(
            classLoader.findClass("javax.net.ssl.TrustManagerFactory"),
            "getTrustManagers",
        ) {
            it.result = EmptyTrustManager.EMPTY_TRUST_LIST
        }
        findAndHookMethodBefore(
            classLoader.findClass("javax.net.ssl.SSLContext"),
            "init",
            Array<KeyManager>::class.java,
            Array<TrustManager>::class.java,
            SecureRandom::class.java,
        ) {
            it.args[0] = null
            it.args[1] = EmptyTrustManager.EMPTY_TRUST_LIST
            it.args[2] = null
        }

        findAndHookMethodBefore(
            classLoader.findClass("javax.net.ssl.HttpsURLConnection"),
            "setSSLSocketFactory",
            SSLSocketFactory::class.java,
        ) {
            Logger.d(TAG, "hook >> setSSLSocketFactory")
            it.args[0] = XposedHelpers.newInstance(SSLSocketFactory::class.java)
        }

        findAndHookMethodBefore(
            classLoader.findClass("okhttp3.CertificatePinner"),
            "findMatchingPins",
            String::class.java,
        ) {
            Logger.d(TAG, "hook >> findMatchingPins arg0 = %s", it.args)
            it.args[0] = ""
        }
    }

    private fun ClassLoader.findClass(name: String): Class<*>? {
        return XposedHelpers.findClassIfExists(name, this)
    }
}
