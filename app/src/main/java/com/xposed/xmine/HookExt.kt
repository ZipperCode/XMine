package com.xposed.xmine

import de.robv.android.xposed.XC_MethodHook
import java.lang.reflect.InvocationHandler
import java.lang.reflect.Method

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/16
 */

inline fun newMethodBefore(
    crossinline before: ((XC_MethodHook.MethodHookParam) -> Unit),
): XC_MethodHook {
    return object : XC_MethodHook() {
        override fun beforeHookedMethod(param: MethodHookParam?) {
            runCatch {
                if (param != null) {
                    before(param)
                }
            }
        }
    }
}

inline fun newMethodAfter(
    crossinline after: ((XC_MethodHook.MethodHookParam) -> Unit),
): XC_MethodHook {
    return object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam?) {
            runCatch {
                if (param != null) {
                    after(param)
                }
            }
        }
    }
}

inline fun newMethodHook(
    crossinline before: ((XC_MethodHook.MethodHookParam) -> Unit),
    crossinline after: ((XC_MethodHook.MethodHookParam) -> Unit),
): XC_MethodHook {
    return object : XC_MethodHook() {

        override fun beforeHookedMethod(param: MethodHookParam?) {
            runCatch {
                if (param != null) {
                    before(param)
                }
            }
        }

        override fun afterHookedMethod(param: MethodHookParam?) {
            runCatch {
                if (param != null) {
                    after(param)
                }
            }
        }
    }
}

inline fun newInvocation(crossinline block: (proxy: Any, method: Method, args: Array<out Any>) -> Any): InvocationHandler {
    return InvocationHandler { proxy, method, args -> block(proxy, method, args) }
}

inline fun <T> runCatch(crossinline block: () -> T): Result<T> {
    return runCatching(block).onFailure {
        Logger.printStackTrace(it)
    }
}
