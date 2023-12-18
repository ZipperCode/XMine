package com.xposed.xmine.hooker

import android.content.Context
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
object BaiduAd : IHookRewardMixin {

    override val TAG: String
        get() = "BD_AD"

    override fun hookReward() {
        runCatch {
            i("Hook百度广告激励方法")
            val rewardCls = XRuntime.loadClass("com.baidu.mobads.sdk.api.RewardVideoAd")
            val rewardListenerCls = XRuntime.loadClass("com.baidu.mobads.sdk.api.RewardVideoAd\$RewardVideoAdListener")
            val rewardMethod = rewardListenerCls.getDeclaredMethod("onRewardVerify", Boolean::class.java)
            val videoCompleteMethod = rewardListenerCls.getDeclaredMethod("playCompletion")

            val adAct = XRuntime.loadClass("com.baidu.mobads.sdk.api.MobRewardVideoActivity")
            XposedHelpers.findAndHookConstructor(
                rewardCls,
                Context::class.java,
                String::class.java,
                rewardListenerCls,
                Boolean::class.java,
                newMethodBefore {
                    val listener = it.args[2]
                    it.args[2] = Proxy.newProxyInstance(
                        rewardListenerCls.classLoader!!,
                        arrayOf(rewardListenerCls),
                    ) { _, method, args ->
                        val res = method.invoke(listener, *args)
                        if (method.name == "onAdShow") {
                            i("广告show延迟1s关闭广告窗口")
                            XRuntime.mainHandler.postDelayed({
                                i("1s后调用reward回调和onVideoComplete回调")
                                rewardMethod.invoke(listener, true)
                                videoCompleteMethod.invoke(listener)
                                ActivityStackManager.safeFinishTopAct { act ->
                                    act.javaClass.isAssignableFrom(adAct)
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
