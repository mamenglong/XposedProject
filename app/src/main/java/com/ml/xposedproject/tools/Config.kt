package com.ml.xposedproject.tools

import android.content.ContentValues
import android.content.Context
import com.ml.xposedproject.provider.ConfigContentProvider
import com.ml.xposedproject.provider.MyCursor
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
    fun<T> getValue(context: Context,key: String,default:T):T{
        val vType = when(default){
            is String -> "String"
            is Int -> "Int"
            is Boolean -> "Boolean"
            is Long -> "Long"
            is Float -> "Float"
            is ByteArray -> "ByteArray"
            else -> throw Exception("不支持此数据类型")
        }
        val cu = context.contentResolver.query(ConfigContentProvider.CONTENT_URI, emptyArray(),key,
            emptyArray(),vType)
        cu as MyCursor
        return  cu.getValue(key,default)
    }
    fun setValue(context: Context,key: String,value: Any){
        val values =ContentValues()
        when(value){
            is String-> values.put(key, value)
            is Int-> values.put(key, value)
            is Boolean-> values.put(key, value)
            is ByteArray-> values.put(key, value)
            is Float-> values.put(key, value)
            is Long-> values.put(key, value)
        }
        context.contentResolver.insert(ConfigContentProvider.CONTENT_URI,values)
    }
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
        const val ENABLE_HX = "ENABLE_HX"
        const val ENABLE_FTQ = "ENABLE_FTQ"
        const val ENABLE_EXPORT = "ENABLE_EXPORT"
        const val ENABLE_HXMH = "ENABLE_HXMH"
        const val ENABLE_O_P_M_A = "ENABLE_O_P_M_A"
        const val ENABLE_XCYS = "ENABLE_XCYS"
        const val ENABLE_HLW = "ENABLE_HLW"
    }
}