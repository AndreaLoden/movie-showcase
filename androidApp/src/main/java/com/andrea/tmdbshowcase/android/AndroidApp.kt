package com.andrea.tmdbshowcase.android

import android.app.Application
import com.andrea.tmdbshowcase.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }
}
