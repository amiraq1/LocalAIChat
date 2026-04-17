package com.localaichat

import android.app.Application
import com.localaichat.di.AppContainer

class LocalAIChatApplication : Application() {
    val container: AppContainer by lazy {
        AppContainer(this)
    }
}
