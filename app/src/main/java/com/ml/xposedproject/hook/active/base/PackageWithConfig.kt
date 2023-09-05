package com.ml.xposedproject.hook.active.base

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import com.google.gson.JsonObject
import com.google.gson.JsonParser
import com.ml.xposedproject.hook.ext.findAndHookMethod
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.showToast
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONObject

abstract class PackageWithConfig : HookPackage {
    protected lateinit var mSharedPreferences: SharedPreferences
    protected val mConfig: String?
        get() = mSharedPreferences.getString("config", null)
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log(
            "hookPackage processName:${loadPackageParam.processName},packageName:${loadPackageParam.packageName}",
            this
        )
        if (loadPackageParam.processName != getPackage() || loadPackageParam.appInfo.loadLabel(context!!.packageManager)!= label) {
            return
        }
        mSharedPreferences = context!!.getSharedPreferences(this::class.simpleName, Context.MODE_PRIVATE)
        val config = mConfig
        if (config == null) {
            showToast("首次使用,配置初始化ing...")
            init(loadPackageParam)
            return
        }
        log("config:$config", this)
        val jsonObject =
            JsonParser.parseString(config).asJsonObject
        if (!isCurrentVersion(jsonObject)) {
            showToast("检测到版本改变,自适配中...")
            init(loadPackageParam)
        } else {
            exec(loadPackageParam, jsonObject)
        }
    }

    protected fun saveConfig(block: (JSONObject) -> Unit) {
        val jSONObject = JSONObject()
        jSONObject.put("versionCode", versionCode)
        jSONObject.put("versionName", versionName)
        block.invoke(jSONObject)
        mSharedPreferences.edit()
            .putString("config", jSONObject.toString()).apply()
    }

    protected fun isCurrentVersion(jsonObject: JsonObject): Boolean {
        return jsonObject.get("versionCode").asLong == versionCode
    }

    abstract fun init(loadPackageParam: LoadPackageParam)
    abstract fun exec(loadPackageParam: LoadPackageParam, jsonObject: JsonObject)
}