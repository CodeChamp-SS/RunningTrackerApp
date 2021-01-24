package com.saarthak.runningtrackerapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import timber.log.Timber

@HiltAndroidApp
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        // setting up timber
        Timber.plant(Timber.DebugTree())
    }

}