package com.ml.xposedproject.tools

import android.content.ContentValues
import android.content.Context
import com.ml.xposedproject.provider.ConfigContentProvider
import com.tencent.mmkv.MMKV

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:06
 * Description: This is Config
 * Package: com.ml.xposedproject.tools
 * Project: XposedProject
 */
object Config {
    fun getBool(context: Context,key: String):Boolean{
        val value = context.contentResolver.update(ConfigContentProvider.CONTENT_URI,ContentValues(),key,null)
        return value==1
    }
    fun setBool(context: Context,key: String,boolean: Boolean){
        val values =ContentValues()
        values.put(key, boolean)
        context.contentResolver.insert(ConfigContentProvider.CONTENT_URI,values)
    }
    object KEYS{
        const val ENABLE_XYJMH = "ENABLE_XYJMH"
        const val ENABLE_DY = "ENABLE_DY"
    }
}