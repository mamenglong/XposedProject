package com.ml.xposedproject.tools

import android.content.ContentValues
import android.content.Context
import android.content.SharedPreferences
import android.os.Process
import android.util.Log
import androidx.core.content.edit
import com.ml.xposedproject.BuildConfig
import com.ml.xposedproject.log
import com.ml.xposedproject.provider.ConfigContentProvider
import com.ml.xposedproject.provider.MyCursor
import com.tencent.mmkv.MMKV
import de.robv.android.xposed.XSharedPreferences
import kotlin.concurrent.thread
import kotlin.math.min

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:06
 * Description: This is Config
 * Package: com.ml.xposedproject.tools
 * Project: XposedProject
 */
object Config {
    lateinit var mineSharedPreferences: SharedPreferences
    lateinit var xSharedPreferences: XSharedPreferences
    private val init: Boolean
        get() = this::xSharedPreferences.isInitialized
    private val initMain: Boolean
        get() = this::mineSharedPreferences.isInitialized

    fun initMain(context: Context) {
        Log.d(
            "SharedPreferences",
            "initMain:${initMain} process:${Process.myPid()} thread:${Thread.currentThread().id}"
        )
        if (initMain.not()) {
            mineSharedPreferences =
                context.getSharedPreferences(
                    "${BuildConfig.APPLICATION_ID}_preferences",
                    Context.MODE_PRIVATE
                )
            Log.d(
                "SharedPreferences",
                "init:${initMain} process:${Process.myPid()} thread:${Thread.currentThread().id} path:${mineSharedPreferences}"
            )
        }
    }

    fun init() {
        log(
            "init:${init} process:${Process.myPid()} thread:${Thread.currentThread().id}",
            this
        )
        if (init.not()) {
            xSharedPreferences = XSharedPreferences(BuildConfig.APPLICATION_ID)
            xSharedPreferences.makeWorldReadable()
            log(
                "init:${init} process:${Process.myPid()} thread:${Thread.currentThread().id} path:${xSharedPreferences.file.absolutePath}",
                this
            )
        }
        var ss = ""
        xSharedPreferences.all.forEach { t, any ->
            ss += "$t:$any "
        }
        log("ss:$ss", this)
        xSharedPreferences.reload()
    }

    private fun <T> getValue(key: String, default: T, main: Boolean): T {
        return kotlin.runCatching {
            val it = if (main) mineSharedPreferences else xSharedPreferences
            val result =
                when (default) {
                    is String -> it.getString(key, default) ?: default
                    is Boolean -> it.getBoolean(key, default)
                    is Int -> it.getInt(key, default)
                    is Long -> it.getLong(key, default)
                    is Float -> it.getFloat(key, default)
                    is Set<*> -> it.getStringSet(key, emptySet())
                    else -> throw Exception("不支持此数据类型")
                }
            result as T
        }.fold(
            onSuccess = {
                log(

                    "getValue(${key}:${it})",
                    this
                )
                it
            },
            onFailure = {
                log(
                    "getValue(${key}:${it}) throw:${it}",
                    this
                )
                default
            }
        )

    }

    private fun <T> getValue(context: Context, key: String, default: T): T {
        val vType = when (default) {
            is String -> "String"
            is Int -> "Int"
            is Boolean -> "Boolean"
            is Long -> "Long"
            is Float -> "Float"
            is ByteArray -> "ByteArray"
            else -> throw Exception("不支持此数据类型")
        }
        val cu = context.contentResolver.query(
            ConfigContentProvider.CONTENT_URI, emptyArray(), key,
            emptyArray(), vType
        )
        cu as MyCursor
        return cu.getValue(key, default)
    }

    private fun setValue(context: Context, key: String, value: Any) {
        val values = ContentValues()
        when (value) {
            is String -> values.put(key, value)
            is Int -> values.put(key, value)
            is Boolean -> values.put(key, value)
            is ByteArray -> values.put(key, value)
            is Float -> values.put(key, value)
            is Long -> values.put(key, value)
        }
        context.contentResolver.insert(ConfigContentProvider.CONTENT_URI, values)
    }

    private fun setValue(key: String, value: Any, main: Boolean) {
        val p = if (main) mineSharedPreferences else xSharedPreferences
        p.edit(true) {
            when (value) {
                is String -> putString(key, value)
                is Boolean -> putBoolean(key, value)
                is Int -> putInt(key, value)
                is Long -> putLong(key, value)
                is Float -> putFloat(key, value)
                is Set<*> -> putStringSet(key, value as Set<String>)
                else -> throw Exception("不支持此数据类型")
            }
        }
    }

    fun getBool(context: Context, key: String): Boolean {
        val value = context.contentResolver.update(
            ConfigContentProvider.CONTENT_URI,
            ContentValues(),
            key,
            null
        )
        return value == 1
    }

    fun getBool(key: String, default: Boolean = false, main: Boolean = false): Boolean {
        return getValue(key, default, main)
    }

    fun setBool(context: Context, key: String, boolean: Boolean) {
        val values = ContentValues()
        values.put(key, boolean)
        context.contentResolver.insert(ConfigContentProvider.CONTENT_URI, values)
    }

    fun setBool(key: String, boolean: Boolean, main: Boolean = false) {
        setValue(key, value = boolean, main)
    }
}