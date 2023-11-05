package com.xposed.xmine.hooker

import com.xposed.xmine.ActivityStackManager
import com.xposed.xmine.Logger
import com.xposed.xmine.XRuntime
import com.xposed.xmine.hooker.base.IHookRewardMixin
import com.xposed.xmine.newMethodAfter
import com.xposed.xmine.newMethodBefore
import com.xposed.xmine.runCatch
import de.robv.android.xposed.XposedHelpers
import java.lang.reflect.Proxy

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object KsAd : IHookRewardMixin {

    override val TAG: String
        get() = "KS_AD"

    private var sdkManagerObj: Any? = null

    override fun hookReward() {
        runCatch {
            val interClsName = "com.kwad.sdk.api.KsRewardVideoAd"
            val implClsName = "com.kwad.components.ad.reward.d"
            val rewardInterfaceCls = XRuntime.loadClass(interClsName)
            val rewardCls = XRuntime.loadClass(implClsName)
            if (rewardCls.isAssignableFrom(rewardInterfaceCls)) {
                e("错误，$implClsName 不是 $interClsName 的实现类")
                return@runCatch
            }

            val rewardListenerCls = XRuntime.loadClass("com.kwad.sdk.api.KsLoadManager\$RewardVideoAdListener")
            val rewardMethod = rewardListenerCls.getDeclaredMethod("onRewardVerify")
            val videoCompleteMethod = rewardListenerCls.getDeclaredMethod("onVideoPlayEnd")

            val methodCallback = newMethodBefore {
                val listener = it.args[0]
                it.args[0] = Proxy.newProxyInstance(
                    rewardListenerCls.classLoader!!,
                    arrayOf(rewardListenerCls),
                ) { _, method, args ->
                    val res = method.invoke(listener, *args)
                    if (method.name == "onVideoPlayStart") {
                        i("广告 onVideoPlayStart 延迟1s关闭广告窗口")
                        XRuntime.mainHandler.postDelayed({
                            i("1s后调用reward回调和onVideoComplete回调")
                            rewardMethod.invoke(listener)
                            videoCompleteMethod.invoke(listener)
                            ActivityStackManager.safeFinishTopAct { act ->
                                act.javaClass.name.contains("com.kwad.sdk.api.proxy.app.KsRewardVideoActivity") ||
                                        act.javaClass.name.contains("com.kwad.sdk.api.proxy.app.KSRewardLandScapeVideoActivity")
                            }
                        }, 1000)
                    }
                    res
                }
            }

            XposedHelpers.findAndHookMethod(
                rewardCls,
                "setRewardAdInteractionListener",
                rewardListenerCls,
                methodCallback,
            )

            XposedHelpers.findAndHookMethod(
                rewardCls,
                "setRewardPlayAgainInteractionListener",
                rewardListenerCls,
                methodCallback,
            )
        }

        runCatch {
            val cls = XRuntime.classLoader.loadClass("com.kwad.components.ad.reward.l.d")
            val adCls = XRuntime.classLoader.loadClass("com.kwad.components.ad.j.a")
            XposedHelpers.findAndHookConstructor(cls, Int::class.java, adCls, newMethodBefore {

            })
        }

        runCatch {
            val adTemplateCls = XRuntime.loadClass("com.kwad.sdk.core.response.model.AdTemplate")
            XposedHelpers.findAndHookMethod(adTemplateCls, "getmCurPlayTime", newMethodAfter {
                val res = it.result
                d("AdTemplate获取当前播放时间 = %s", res)
                it.result = 100 * 1000L
            })

            XposedHelpers.findAndHookMethod(adTemplateCls, "setmCurPlayTime", newMethodBefore {
                d("AdTemplate设置当前播放时间 = %s", it.args[0])
                it.args[0] = 100 * 1000L
            })
        }
    }
}
