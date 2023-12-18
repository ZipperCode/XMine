package com.xposed.xmine.initializer

import android.app.Application
import com.xposed.xmine.SuManager
import com.xposed.xmine.utils.Logger

object ModuleInitializer : BaseInitializer<Application>() {

    const val TAG = "ModuleInitializer"

    init {
        Logger.d("ModuleInitializer", "init $this")
    }

    override fun init(param: Application) {
        this.application = param

        SuManager.bindService(application.baseContext) { rootAidl ->
            Logger.d(TAG, "bindRootService result = $rootAidl")
            iRootInterface = rootAidl
            if (rootAidl != null) {
                onRootBindSuccess(application.baseContext)
            } else {
                onRootUnbind(application.baseContext)
            }
        }
    }

    @JvmStatic
    fun isActive(): Boolean {
        return false
    }

    override val tag: String get() = "ModuleInitializer"
}
