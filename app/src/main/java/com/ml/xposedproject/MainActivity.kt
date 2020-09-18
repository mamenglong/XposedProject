package com.ml.xposedproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import dalvik.system.PathClassLoader
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv_text.text = getInfo()
        assets.open("classes.dex").use {input->
            val file = File(externalCacheDir,"classes.dex")
            if (file.exists()){
                file.delete()
            }
            file.outputStream().use {
                input.copyTo(it)
                Toast.makeText(this,"复制成功",Toast.LENGTH_SHORT).show()
            }
        }
        kotlin.runCatching {
            val file = File(externalCacheDir,"classes.dex")
            val pathClassLoader =
                PathClassLoader(file.absolutePath, ClassLoader.getSystemClassLoader())
            val cls= Class.forName(MainActivity::class.java.name, true, pathClassLoader)
            val instance = cls.newInstance() as MainActivity
            tv_text.text = instance.getInfo1()
        }.onFailure {
            it.printStackTrace()
        }

    }
    fun getInfo(): String {
        return "hahahha11"
    }
    fun getInfo1(): String {
        return "hahahha11"
    }
}