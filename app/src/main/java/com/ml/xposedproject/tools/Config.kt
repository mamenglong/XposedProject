package com.ml.xposedproject.tools

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
    private val kv = MMKV.defaultMMKV()
    fun getBool(key: String): Boolean {
       return kv.decodeBool(key,false)
    }
    fun setBool(key: String,boolean: Boolean){
        kv.encode(key,boolean)
    }
    object KEYS{
        const val ENABLE_XYJMH = "ENABLE_XYJMH"
        const val ENABLE_DY = "ENABLE_DY"
    }
}