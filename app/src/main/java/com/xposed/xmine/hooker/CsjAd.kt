package com.xposed.xmine.hooker

import android.os.Bundle
import com.xposed.xmine.ActivityStackManager
import com.xposed.xmine.XRuntime
import com.xposed.xmine.hooker.base.IHookRewardMixin
import com.xposed.xmine.utils.newMethodBefore
import com.xposed.xmine.utils.runCatch
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Proxy

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object CsjAd : IHookRewardMixin {

    override val TAG: String
        get() = "CSJ_AD"

    override fun hookReward() {
//        runCatch {
//            DexKitBridge.create(XRuntime.apkPath)?.use {
//                val findClassList = it.findClass {
//                    matcher {
//                        interfaces {
//                            add("com.bytedance.sdk.openadsdk.TTRewardVideoAd")
//                        }
//                    }
//                }
//
//                i("findClassList = %s", findClassList)
//            }
//        }

        run()
    }

    private fun run() {
        runCatch {
            i("Hook穿山甲广告激励方法")
            val rewardInterfaceCls = XRuntime.loadClass("com.bytedance.sdk.openadsdk.TTRewardVideoAd")
            val rewardCls = XRuntime.loadClass("com.ae.i.k.t.c.a.j")
            if (rewardCls.isAssignableFrom(rewardInterfaceCls)) {
                e("错误，com.ae.i.k.t.c.a.j 不是 com.bytedance.sdk.openadsdk.TTRewardVideoAd 的实现类")
                return@runCatch
            }

            val rewardListenerCls = XRuntime.loadClass("com.bytedance.sdk.openadsdk.TTRewardVideoAd\$RewardAdInteractionListener")
            val rewardMethod = rewardListenerCls.getDeclaredMethod("onRewardArrived", Boolean::class.java, Int::class.java, Bundle::class.java)
            val videoCompleteMethod = rewardListenerCls.getDeclaredMethod("onVideoComplete")

            XposedHelpers.findAndHookMethod(
                rewardCls,
                "setRewardAdInteractionListener",
                rewardListenerCls,
                newMethodBefore {
                    val listener = it.args[0]
                    it.args[0] = Proxy.newProxyInstance(
                        rewardListenerCls.classLoader!!,
                        arrayOf(rewardListenerCls),
                    ) { _, method, args ->
                        val res = method.invoke(listener, *args)
                        if (method.name == "onAdShow") {
                            i("广告show延迟1s关闭广告窗口")
                            XRuntime.mainHandler.postDelayed({
                                i("1s后调用reward回调和onVideoComplete回调")
                                rewardMethod.invoke(listener, true, 0, Bundle())
                                videoCompleteMethod.invoke(listener)
                                ActivityStackManager.safeFinishTopAct { act ->
                                    act.javaClass.name.contains("com.bytedance.sdk.openadsdk.stub.activity") ||
                                        act.javaClass.name.contains("com.bytedance.android.openliveplugin.stub.activity")
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
