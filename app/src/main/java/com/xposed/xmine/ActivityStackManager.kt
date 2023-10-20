package com.xposed.xmine

import android.app.Activity
import android.app.Application
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 *
 * @author zhangzhipeng
 * @date 2023/10/17
 */
object ActivityStackManager : Application.ActivityLifecycleCallbacks {

    private val actStackList = mutableListOf<WeakReference<Activity>>()
    override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit

    override fun onActivityStarted(activity: Activity) {
        actStackList.add(WeakReference(activity))
    }

    override fun onActivityResumed(activity: Activity) = Unit

    override fun onActivityPaused(activity: Activity) = Unit

    override fun onActivityStopped(activity: Activity) {
        val iter = actStackList.iterator()
        while (iter.hasNext()) {
            val act = iter.next().get()
            if (act == activity) {
                iter.remove()
                break
            }
        }
    }

    override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit

    override fun onActivityDestroyed(activity: Activity) {
        val iter = actStackList.iterator()
        while (iter.hasNext()) {
            val act = iter.next().get()
            if (act == null || act.isDestroyed) {
                iter.remove()
            }
        }
    }

    fun findTopAct(): Activity? {
        return actStackList.lastOrNull()?.get()
    }

    fun safeFinishTopAct(condition: (Activity) -> Boolean) {
        try {
            val act = findTopAct() ?: return
            if (condition(act) && !act.isFinishing && !act.isDestroyed) {
                act.finish()
            }
        } catch (e: Exception) {
            Logger.printStackTrace(e)
        }
    }
}
