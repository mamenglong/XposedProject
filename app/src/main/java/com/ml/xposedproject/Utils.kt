package com.ml.xposedproject

import android.app.AndroidAppHelper
import android.util.Log
import android.widget.Toast
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedBridge

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
