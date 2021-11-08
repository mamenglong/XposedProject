package com.ml.xposedproject.service

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import android.os.PersistableBundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ml.xposedproject.provider.ConfigContentProvider
import com.tencent.mmkv.MMKV

class AliveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("AliveActivity","isEnable:${ConfigContentProvider.isEnable}")
        onBackPressed()
    }
}