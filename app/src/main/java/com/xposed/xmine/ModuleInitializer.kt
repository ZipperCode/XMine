package com.xposed.xmine

import android.app.Application
import android.content.Context
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

object ModuleInitializer : BaseInitializer() {
    private const val TAG = "Module"
    override val tag: String get() = "ModuleInitializer"

    override fun init(loadPackageParam: LoadPackageParam) {
        super.init(loadPackageParam)
    }

    override fun onAttachBaseContext(application: Application, baseContext: Context) {
        super.onAttachBaseContext(application, baseContext)
    }

    fun isActive(): Boolean {
        return false
    }
}
