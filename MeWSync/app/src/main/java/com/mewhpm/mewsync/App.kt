package com.mewhpm.mewsync

import android.app.Application

class App : Application() {
    companion object {
        private var _app : App? = null
        fun getAppContext() = _app!!.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        _app = this
    }
}