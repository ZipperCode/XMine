package com.xposed.xmine.hooker

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
object TanxAd : IHookRewardMixin {

    override val TAG: String
        get() = "Tanx_AD"

    override fun hookReward() {
        runCatch {
            val rewardCls = XRuntime.loadClass("com.alimm.tanx.ui.ad.express.reward.tanxu_while")
            val rewardListenerCls = XRuntime.loadClass("com.alimm.tanx.ui.ad.express.reward.ITanxRewardExpressAd\$OnRewardAdListener")
            val rewardMethod = rewardListenerCls.getDeclaredMethod("onRewardArrived", Boolean::class.java, Int::class.java, Map::class.java)
            val videoCompleteMethod = rewardListenerCls.getDeclaredMethod("onVideoComplete")

            XposedHelpers.findAndHookMethod(
                rewardCls,
                "setOnRewardAdListener",
                rewardListenerCls,
                newMethodBefore {
                    val listener = it.args[0]
                    it.args[0] = Proxy.newProxyInstance(
                        rewardListenerCls.classLoader!!,
                        arrayOf(rewardListenerCls),
                    ) { _, method, args ->
                        val res = method.invoke(listener, *args)
                        if (method.name == "showAd") {
                            i("广告show延迟1s关闭广告窗口")
                            XRuntime.mainHandler.postDelayed({
                                i("1s后调用reward回调和onVideoComplete回调")
                                rewardMethod.invoke(listener, true, 0, emptyMap<Any, Any>())
                                videoCompleteMethod.invoke(listener)
                                ActivityStackManager.safeFinishTopAct { act ->
                                    act.javaClass.name.contains("RewardPortraitActivity") ||
                                        act.javaClass.name.contains("RewardVideoPortraitActivity")
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
