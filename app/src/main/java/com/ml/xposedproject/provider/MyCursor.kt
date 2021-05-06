package com.ml.xposedproject.provider

import android.content.ContentResolver
import android.content.ContentValues
import android.database.*
import android.net.Uri
import android.os.Bundle

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2021/5/6 17:52
 * Description: This is MyCursor
 * Package: com.ml.xposedproject.provider
 * Project: XposedProject
 */
class MyCursor(): Cursor{
    private val kv = ContentValues()


    fun <T> getValue(key: String, default: T): T {
        return when (default) {
            is Boolean -> {
                (kv.getAsBoolean(key)?:false) as T
            }
            is String -> {
                (kv.getAsString(key) ?: "") as T
            }
            is Long -> {
                (kv.getAsLong(key)?:0) as T
            }
            is ByteArray -> {
                (kv.getAsByteArray(key)?:byteArrayOf())as T
            }
            is Float -> {
                (kv.getAsFloat(key)) as T
            }
            is Int -> {
                (kv.getAsInteger(key)) as T
            }
            else -> {
                throw Exception("不支持此类型.")
            }
        }
    }

    fun setValue(key: String, value: Any) {
        when (value) {
            is Boolean -> {
                kv.put(key, value)
            }
            is String -> {
                kv.put(key, value)
            }
            is Long -> {
                kv.put(key, value)
            }
            is ByteArray -> {
                kv.put(key, value)
            }
            is Float -> {
                kv.put(key, value)
            }
            is Int -> {
                kv.put(key, value)
            }
            else -> {
                throw Exception("不支持此类型.")
            }
        }
    }

    override fun close() {

    }

    override fun getCount(): Int {
       return -1
    }

    override fun getPosition(): Int {
        return -1
    }

    override fun move(offset: Int): Boolean {
        return false
    }

    override fun moveToPosition(position: Int): Boolean {
        return false
    }

    override fun moveToFirst(): Boolean {
       return false
    }

    override fun moveToLast(): Boolean {
      return false
    }

    override fun moveToNext(): Boolean {
       return false
    }

    override fun moveToPrevious(): Boolean {
        return false
    }

    override fun isFirst(): Boolean {
        return false
    }

    override fun isLast(): Boolean {
        return false
    }

    override fun isBeforeFirst(): Boolean {
        return false
    }

    override fun isAfterLast(): Boolean {
        return false
    }

    override fun getColumnIndex(columnName: String?): Int {
        return 0
    }

    override fun getColumnIndexOrThrow(columnName: String?): Int {
        return 0

    }

    override fun getColumnName(columnIndex: Int): String {
        return ""

    }

    override fun getColumnNames(): Array<String> {
        return arrayOf()
    }

    override fun getColumnCount(): Int {
       return 0
    }

    override fun getBlob(columnIndex: Int): ByteArray {
       return byteArrayOf()
    }

    override fun getString(columnIndex: Int): String {
        return ""
    }

    override fun copyStringToBuffer(columnIndex: Int, buffer: CharArrayBuffer?) {

    }

    override fun getShort(columnIndex: Int): Short {
      return 0
    }

    override fun getInt(columnIndex: Int): Int {
       return 0
    }

    override fun getLong(columnIndex: Int): Long {
       return 0
    }

    override fun getFloat(columnIndex: Int): Float {
      return 0F
    }

    override fun getDouble(columnIndex: Int): Double {
       return 0.0
    }

    override fun getType(columnIndex: Int): Int {
       return 0
    }

    override fun isNull(columnIndex: Int): Boolean {
     return false
    }

    override fun deactivate() {

    }

    override fun requery(): Boolean {
        return false
    }

    override fun isClosed(): Boolean {
       return false
    }

    override fun registerContentObserver(observer: ContentObserver?) {

    }

    override fun unregisterContentObserver(observer: ContentObserver?) {

    }

    override fun registerDataSetObserver(observer: DataSetObserver?) {

    }

    override fun unregisterDataSetObserver(observer: DataSetObserver?) {

    }

    override fun setNotificationUri(cr: ContentResolver?, uri: Uri?) {

    }

    override fun getNotificationUri(): Uri {
        return Uri.parse("")
    }

    override fun getWantsAllOnMoveCalls(): Boolean {
       return false
    }

    override fun setExtras(extras: Bundle?) {

    }

    override fun getExtras(): Bundle {
         return Bundle()
    }

    override fun respond(extras: Bundle?): Bundle {
        return Bundle()
    }

}