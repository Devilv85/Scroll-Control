package com.vishal.thinkpadcontrol

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ThinkPadControlApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}