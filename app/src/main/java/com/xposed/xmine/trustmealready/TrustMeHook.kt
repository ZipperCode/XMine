package com.xposed.xmine.trustmealready

import com.xposed.xmine.utils.Logger
import com.xposed.xmine.utils.runCatch
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Method
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.security.cert.X509Certificate

object TrustMeHook {
    private const val TAG = "TrustMeHook"
    private const val CLASS_NAME = "com.android.org.conscrypt.TrustManagerImpl"
    private const val METHOD_NAME = "checkTrustedRecursive"
    private val SSL_RETURN_TYPE: Class<*> = List::class.java
    private val SSL_RETURN_PARAM_TYPE: Class<*> = X509Certificate::class.java
    fun hook() {
        runCatch {
            Logger.d(TAG, "hook %s", CLASS_NAME)
            var count = 0
            val clazz = XposedHelpers.findClass(CLASS_NAME, null)
            for (declaredMethod in clazz.declaredMethods) {
                if (!checkSslMethod(declaredMethod)) {
                    continue
                }
                val params: MutableList<Any> = ArrayList()
                params.addAll(listOf(declaredMethod.parameterTypes))
                params.add(object : XC_MethodReplacement() {
                    @Throws(Throwable::class)
                    override fun replaceHookedMethod(param: MethodHookParam): Any {
                        return ArrayList<X509Certificate>()
                    }
                })
                XposedHelpers.findAndHookMethod(clazz, declaredMethod.name, params.toTypedArray())
                count++
            }
            Logger.d(TAG, "hook success handle method count = %s", count)
        }
    }

    private fun checkSslMethod(method: Method): Boolean {
        if (method.name != METHOD_NAME) {
            return false
        }

        if (!SSL_RETURN_TYPE.isAssignableFrom(method.returnType)) {
            return false
        }
        val returnType: Type = method.genericReturnType as? ParameterizedType ?: return false
        val args = (returnType as ParameterizedType).actualTypeArguments
        return !(args.size != 1 || !args[0].equals(SSL_RETURN_PARAM_TYPE))
    }
}
