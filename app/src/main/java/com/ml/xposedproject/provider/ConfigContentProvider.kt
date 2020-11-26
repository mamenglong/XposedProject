package com.ml.xposedproject.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.ml.xposedproject.tools.Config
import com.tencent.mmkv.MMKV

class ConfigContentProvider : ContentProvider() {
    private lateinit var kv:MMKV
   companion object{
       const val AUTHORITIES = "com.ml.xposedproject.provider.ConfigContentProvider"
       val CONTENT_URI = Uri.parse("content://$AUTHORITIES")
   }
    fun getBool(key: String): Boolean {
        return kv.getBoolean(key,false)
    }
    fun setBool(key: String,boolean: Boolean){
        kv.putBoolean(key,boolean)
    }
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {

        return 0
    }

    override fun getType(uri: Uri): String? {
     return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val key = values?.keySet()?.toTypedArray()?.get(0)?:""
        Log.d("ConfigContentProvider","insert key:$key")
        if (key.isNotEmpty()) {
            val value = values?.getAsBoolean(key) ?: false
            Log.d("ConfigContentProvider","insert key:$key value:$value")
            setBool(key,value)
        }
        return null
    }

    override fun onCreate(): Boolean {
        Log.d("ConfigContentProvider","onCreate")
        MMKV.initialize(context)
        kv = MMKV.defaultMMKV()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return null
    }

    /**
     * 功能重写
     */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val key = selection
        Log.d("ConfigContentProvider","update  key:$key")
        var result = -1
        if (!key.isNullOrEmpty()) {
            result = if (getBool(key)) 1 else 0
        }
        Log.d("ConfigContentProvider","update key:$key result:$result")
        return result
    }
}