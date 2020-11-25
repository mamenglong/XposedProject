package com.ml.xposedproject

import android.app.Application
import com.tencent.mmkv.MMKV
import dagger.hilt.android.HiltAndroidApp

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 17:47
 * Description: This is App
 * Package: com.ml.xposedproject
 * Project: XposedProject
 */
@HiltAndroidApp
class App:Application() {
    companion object{
        lateinit var application:App
        private set
    }
    override fun onCreate() {
        super.onCreate()
        application = this
        MMKV.initialize(this)
    }
}