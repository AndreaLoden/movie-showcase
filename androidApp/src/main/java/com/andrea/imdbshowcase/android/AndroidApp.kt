package com.andrea.imdbshowcase.android

import android.app.Application
import com.andrea.imdbshowcase.di.initKoin

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        initKoin()
    }
}
