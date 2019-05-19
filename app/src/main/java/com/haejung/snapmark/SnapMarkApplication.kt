package com.haejung.snapmark

import android.app.Application
import timber.log.Timber

class SnapMarkApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        initTimber()
    }

    private fun initTimber() {
        if (BuildConfig.DEBUG)
            Timber.plant(Timber.DebugTree())
    }


}