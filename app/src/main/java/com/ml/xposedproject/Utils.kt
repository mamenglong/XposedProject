package com.ml.xposedproject

import android.app.AndroidAppHelper
import android.content.Context
import android.widget.Toast
import de.robv.android.xposed.XposedBridge
import java.io.File

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/9/16 16:07
 * Description: This is Utils
 * Package: com.ml.xposedproject
 * Project: XposedProject
 */
fun log(msg: String, clazz: Any? = null){
    XposedBridge.log("${clazz?.javaClass?.simpleName ?: "menglong"}->$msg")
   // Log.d(clazz?.javaClass?.simpleName ?: "menglong",msg)
}

fun showToast(msg: String){
    Toast.makeText(AndroidAppHelper.currentApplication(),msg,Toast.LENGTH_SHORT).show()
}

fun Context.showToast(msg: String){
    Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
}
fun taichi(block:()->Unit):Boolean{
    if (BuildConfig.FLAVOR=="taichi"){
        block.invoke()
    }
    return BuildConfig.FLAVOR=="taichi"
}

fun randomString(length: Int): String {
    val str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
    val random = java.util.Random()
    val sb = StringBuffer()
    for (i in 0 until length) {
        val number = random.nextInt(62)
        sb.append(str[number])
    }
    return sb.toString()
}
fun Context.clearData() {
    val file = File(cacheDir.getParent())
    if (file.exists()) {
        file.listFiles().forEach {
            it.deleteRecursively()
        }
    }
}