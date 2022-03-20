package com.ml.xposedproject.taichi

import android.content.ActivityNotFoundException
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.core.content.ContextCompat.startActivity
import java.util.*


object TaiChiUtil{
    fun isExpModuleActive(context: Context): Boolean {
        var isExp = false
        requireNotNull(context) { "context must not be null!!" }
        try {
            val contentResolver: ContentResolver = context.getContentResolver()
            val uri: Uri = Uri.parse("content://me.weishu.exposed.CP/")
            var result: Bundle? = null
            try {
                result = contentResolver.call(uri, "active", null, null)
            } catch (e: RuntimeException) {
                // TaiChi is killed, try invoke
                try {
                    val intent = Intent("me.weishu.exp.ACTION_ACTIVE")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    context.startActivity(intent)
                } catch (e1: Throwable) {
                    return false
                }
            }
            if (result == null) {
                result = contentResolver.call(uri, "active", null, null)
            }
            if (result == null) {
                return false
            }
            isExp = result.getBoolean("active", false)
        } catch (ignored: Throwable) {
        }
        return isExp
    }

    private fun getExpApps(context: Context): List<String>{
        val result: Bundle?
        result = try {
            context.contentResolver.call(
                Uri.parse("content://me.weishu.exposed.CP/"),
                "apps",
                null,
                null
            )
        } catch (e: Throwable) {
            return Collections.emptyList()
        }
        return if (result == null) {
            Collections.emptyList()
        } else result.getStringArrayList("apps")
            ?: return Collections.emptyList()
    }
    fun activeModule(context: Context,packageName:String){
        val t = Intent("me.weishu.exp.ACTION_MODULE_MANAGE")
        t.data = Uri.parse("package:$packageName")
        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(t)
        } catch (e: ActivityNotFoundException) {
            // TaiChi not installed.
        }
    }
    fun addApp(context: Context,vararg packageName:String){
        val t = Intent("me.weishu.exp.ACTION_ADD_APP")
        var number = 0
        val p = packageName.joinToString("|"){
            "package${if (number==0) "" else number++}:$it"
        }
        t.data = Uri.parse(p)
        t.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        try {
            context.startActivity(t)
        } catch (e: ActivityNotFoundException) {
            // TaiChi not installed or version below 4.3.4.
        }
    }
}