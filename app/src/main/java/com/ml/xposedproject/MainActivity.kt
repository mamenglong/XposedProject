package com.ml.xposedproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import dalvik.system.PathClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_text.text = getInfo()
        kotlin.runCatching {
            val file = File(packageCodePath)
            val pathClassLoader =
                PathClassLoader(file.absolutePath, ClassLoader.getSystemClassLoader())
            val cls= Class.forName(XposedInit::class.java.name, true, pathClassLoader)
            val instance = cls.newInstance()
        }.onFailure {
            it.printStackTrace()
        }

    }
    fun getInfo(): String {
        return "hahahha"
    }
}