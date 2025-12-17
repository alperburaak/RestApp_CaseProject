package com.alperburaak.restapp

import android.app.Application
import com.alperburaak.restapp.di.appModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class RestApp : Application() {

    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@RestApp)
            modules(appModule)
        }
    }
}
