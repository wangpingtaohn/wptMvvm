package com.wpt.mvvm.base

import android.app.Application
import android.content.Context

class BaseApplication : Application() {
    override fun onCreate() {
        context = this.applicationContext
        super.onCreate()
    }

    companion object {
        var context: Context? = null
            private set
    }
}