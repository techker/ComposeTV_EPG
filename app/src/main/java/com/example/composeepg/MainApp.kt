package com.example.composeepg

import android.app.Application
import android.content.Context
import timber.log.Timber

class MainApp: Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        mContext = applicationContext
    }
    companion object {
        var mContext: Context? = null
        @JvmStatic
        fun getAppContext(): Context? {
            return mContext
        }
    }
}