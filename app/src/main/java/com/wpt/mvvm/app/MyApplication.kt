package com.wpt.mvvm.app

import android.app.Application
import android.content.Context

class MyApplication : Application() {
    override fun onCreate() {
        context = this.applicationContext
        super.onCreate()
    }

    companion object {
        var context: Context? = null
            private set
    }
}