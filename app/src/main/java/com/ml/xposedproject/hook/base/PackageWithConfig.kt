package com.ml.xposedproject.hook.base

import android.content.SharedPreferences
import com.google.gson.JsonObject
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject

abstract class PackageWithConfig : HookPackage {
    protected lateinit var mSharedPreferences: SharedPreferences
    protected val mConfig: String?
        get() = mSharedPreferences.getString("config", null)

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage:${loadPackageParam.packageName}", this)
        mSharedPreferences = context!!.getSharedPreferences(this::class.simpleName, 0)
    }

    protected fun saveConfig(block: (JSONObject) -> Unit) {
        val jSONObject = JSONObject()
        jSONObject.put("versionCode", versionCode)
        jSONObject.put("versionName", versionName)
        block.invoke(jSONObject)
        mSharedPreferences.edit()
            .putString("config", jSONObject.toString()).apply()
    }

    protected fun isCurrentVersion(jsonObject: JsonObject):Boolean{
        return jsonObject.get("versionCode").asLong == versionCode
    }
}