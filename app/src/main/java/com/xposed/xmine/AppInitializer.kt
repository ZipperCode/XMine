package com.xposed.xmine

import android.app.Application
import android.content.Context

object AppInitializer : BaseInitializer() {

    override val tag: String get() = "AppInitializer"

    override fun onAttachBaseContext(application: Application, baseContext: Context) {
        super.onAttachBaseContext(application, baseContext)
    }

    override fun onRootBindSuccess(baseContext: Context) {
        super.onRootBindSuccess(baseContext)
    }

    override fun onRootUnbind(baseContext: Context) {
        super.onRootUnbind(baseContext)
    }
}
