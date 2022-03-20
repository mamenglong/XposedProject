package com.ml.xposedproject.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import android.util.Log
import com.ml.xposedproject.BuildConfig
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.service.AliveActivity
import com.tencent.mmkv.MMKV
import java.util.Collection

class ConfigContentProvider : ContentProvider() {
    private lateinit var kv: MMKV

    companion object {
        var isEnable = false
        const val AUTHORITIES = "${BuildConfig.APPLICATION_ID}.provider.ConfigContentProvider"
        val CONTENT_URI = Uri.parse("content://$AUTHORITIES")
    }

    fun <T> getValue(key: String, default: T): T {
        return when (default) {
            is Boolean -> {
                kv.getBoolean(key, false) as T
            }
            is String -> {
                (kv.getString(key, "") ?: "") as T
            }
            is Long -> {
                kv.getLong(key, 0L) as T
            }
            is ByteArray -> {
                kv.getBytes(key, byteArrayOf()) as T
            }
            is Float -> {
                kv.getFloat(key, 0F) as T
            }
            is Int -> {
                kv.getInt(key, 0) as T
            }
            is Set<*> -> {
                (kv.getStringSet(key, setOf()) ?: setOf<Any>()) as T
            }
            else -> {
                throw Exception("不支持此类型.")
            }
        }
    }

    fun setValue(key: String, value: Any) {
        when (value) {
            is Boolean -> {
                kv.putBoolean(key, value)
            }
            is String -> {
                kv.putString(key, value)
            }
            is Long -> {
                kv.putLong(key, value)
            }
            is ByteArray -> {
                kv.putBytes(key, value)
            }
            is Float -> {
                kv.putFloat(key, value)
            }
            is Int -> {
                kv.putInt(key, value)
            }
            is Set<*> -> {
                kv.putStringSet(key, value as Set<String>)
            }
            else -> {
                throw Exception("不支持此类型.")
            }
        }
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return 0
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val key = values?.keySet()?.toTypedArray()?.get(0) ?: ""
        Log.d("ConfigContentProvider", "insert key:$key")
        if (key.isNotEmpty()) {
            val value = values?.get(key)
            Log.d("ConfigContentProvider", "insert key:$key value:$value")
            value?.let {
                setValue(key, value)
            }
        }
        return null
    }

    override fun onCreate(): Boolean {
        Log.d("ConfigContentProvider", "onCreate")
        MMKV.initialize(context)
        isEnable = true
        kv = MMKV.defaultMMKV()
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor {
        val cu = MyCursor()
        val key = selection!!
        val v = when {
            sortOrder.equals("String")  -> {
                getValue(selection, "")
            }
            sortOrder.equals("Int") -> {
                getValue(selection, 0)
            }
            sortOrder.equals("Float")-> {
                getValue(selection, 0F)
            }
            sortOrder.equals("Long") -> {
                getValue(selection, 0L)
            }
            sortOrder.equals("Boolean") -> {
                getValue(selection, false)
            }
            sortOrder.equals("ByteArray") -> {
                getValue(selection, byteArrayOf())
            }
            else ->{
                throw Exception("不支持此数据类型")
            }
        }
        cu.setValue(key,v)
        return cu
    }

    /**
     * 功能重写
     */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        val key = selection
        Log.d("ConfigContentProvider", "update  key:$key")
        var result = -1
        if (!key.isNullOrEmpty()) {
            result = if (getValue(key, false)) 1 else 0
        }
        Log.d("ConfigContentProvider", "update key:$key result:$result")
        return result
    }
}