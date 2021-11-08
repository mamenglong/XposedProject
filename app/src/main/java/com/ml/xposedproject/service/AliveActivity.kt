package com.ml.xposedproject.service

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.ml.xposedproject.provider.ConfigContentProvider
import com.ml.xposedproject.showToast

class AliveActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        overridePendingTransition(0,0)
        Log.d("AliveActivity","isEnable:${ConfigContentProvider.isEnable}")
        showToast("Xposed项目已激活")
        onBackPressed()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0,0)
    }
}