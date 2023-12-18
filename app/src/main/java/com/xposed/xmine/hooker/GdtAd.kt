package com.xposed.xmine.hooker

import android.content.Context
import com.xposed.xmine.ActivityStackManager
import com.xposed.xmine.XRuntime
import com.xposed.xmine.hooker.base.IHookRewardMixin
import com.xposed.xmine.utils.newMethodBefore
import com.xposed.xmine.utils.runCatch
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Proxy
import java.util.UUID

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object GdtAd : IHookRewardMixin {

    private var adActCls: Class<*>? = null

    override val TAG: String
        get() = "GDT_AD"

    private const val AD_ACT = "com.qq.e.ads.ADActivity"

    fun noInit() {
        runCatch {
            val gdtCls = XRuntime.loadClass("com.qq.e.comm.managers.GDTAdSdk")
            XposedHelpers.findAndHookMethod(
                gdtCls,
                "init",
                Context::class.java,
                String::class.java,
                object : XC_MethodReplacement() {
                    override fun replaceHookedMethod(param: MethodHookParam?): Any {
                        i("替换广点通初始化方法，阻止sdk初始化")
                        return Unit
                    }
                },
            )
        }
    }

    override fun hookReward() {
        runCatch {
            i("Hook百度广告激励方法")
            val rewardCls = XRuntime.loadClass("com.qq.e.ads.rewardvideo.RewardVideoAD")
            val rewardListenerCls = XRuntime.loadClass("com.qq.e.ads.rewardvideo.RewardVideoADListener")
            val rewardMethod = rewardListenerCls.getDeclaredMethod("onReward", Map::class.java)
            val videoCompleteMethod = rewardListenerCls.getDeclaredMethod("onVideoComplete")

            val adActCls = XRuntime.loadClass(AD_ACT)

            XposedHelpers.findAndHookConstructor(
                rewardCls,
                rewardListenerCls,
                Boolean::class.java,
                newMethodBefore {
                    val listener = it.args[0]
                    it.args[0] = Proxy.newProxyInstance(
                        rewardListenerCls.classLoader!!,
                        arrayOf(rewardListenerCls),
                    ) { _, method, args ->
                        val res = method.invoke(listener, *args)
                        if (method.name == "onADShow") {
                            i("广告show延迟1s关闭广告窗口")
                            XRuntime.mainHandler.postDelayed({
                                i("1s后调用reward回调和onVideoComplete回调")
                                rewardMethod.invoke(listener, mapOf<String, Any>("transId" to UUID.randomUUID().toString().split("-", "")))
                                videoCompleteMethod.invoke(listener)
                                ActivityStackManager.safeFinishTopAct { act ->
                                    act.javaClass.isAssignableFrom(adActCls)
                                }
                            }, 1000)
                        }
                        res
                    }
                },
            )
        }
    }
}
