package com.ml.xposedproject.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.tencent.mmkv.MMKV

class AliveService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        MMKV.initialize(this)
    }
}