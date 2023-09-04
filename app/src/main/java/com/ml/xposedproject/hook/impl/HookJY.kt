package com.ml.xposedproject.hook.impl

import android.content.Context
import android.content.SharedPreferences
import com.google.auto.service.AutoService
import com.google.gson.JsonParser
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.hook.base.PackageWithConfig
import com.ml.xposedproject.hook.ext.findClass
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import com.ml.xposedproject.showToast
import dalvik.system.DexFile
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.lang.Boolean
import java.lang.reflect.Modifier
import kotlin.String
import kotlin.Throwable
import kotlin.apply

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
@AutoService(HookPackage::class)
class HookJY : PackageWithConfig() {
    override val label: String = "剪映"
    override fun getPackage(): String {
        return "com.lemon.lv"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        super.hookCurrentPackage(loadPackageParam)
        loadPackageParam.hookAndReplaceMethod(
            "com.lm.components.subscribe.config.UserVipInfo",
            "isVipUser", true
        )
        val config = mConfig
        if (config == null) {
            showToast("首次使用,配置初始化ing...")
            init(loadPackageParam)
            return
        }
        log("config:$config",this)
        val jsonObject =
            JsonParser.parseString(config).asJsonObject
        if (isCurrentVersion(jsonObject)) {
            showToast("检测到版本改变,自适配中...")
            init(loadPackageParam)
        } else {
            val list = jsonObject.get("list").asJsonArray
            list.forEach {
                val ob = it.asJsonObject
                val cls = ob.get("class").asString
                val me = ob.get("method").asString
                loadPackageParam.hookAndReplaceMethod(cls,me,true)
            }
        }

    }

    private fun init(loadPackageParam: LoadPackageParam) {
        val list = JSONArray()
        for (str in getClassNames(context!!)) {
            for (field in loadPackageParam.findClass(str)!!.declaredFields) {
                if (field.type == Boolean.TYPE) {
                    for (annotation in field.annotations) {
                        try {
                            if (annotation.toString().contains("is_vip")
                                || annotation.toString().contains("has_purchased")
                                || annotation.toString().contains("hasPurchased")
                                || annotation.toString().contains("vip_flag")
                            ) {
                                val declaringClass = field.declaringClass
                                log("init class name:${declaringClass.name},annotation:${annotation.toString()}", this)
                                for (method in declaringClass.getDeclaredMethods()) {
                                    val modifiers = method.modifiers
                                    if (!Modifier.isAbstract(modifiers)
                                        && !Modifier.isInterface(modifiers)
                                        && method.returnType.toString().contains("boolean")
                                        && method.parameterCount == 0
                                    ) {
                                        log("init Method name:${method.name}", this)
                                        val c = method.declaringClass.getName()
                                        val m = method.name
                                        list.put(JSONObject().apply {
                                            put("class", c)
                                            put("method", m)
                                        })
                                        XposedBridge.hookMethod(method, object : XC_MethodHook() {
                                            override fun afterHookedMethod(methodHookParam: MethodHookParam) {
                                                methodHookParam.setResult(true)
                                            }
                                        })
                                    }
                                }
                            }
                        } catch (th: Throwable) {
                            log("init error:${th.message}", this)
                        }
                    }
                }
            }
        }
        try {
            saveConfig(){
                it.put("list", list)
            }
            showToast("适配完成...")
        } catch (e: JSONException) {
        }
    }

    private fun getClassNames(context: Context): List<String> {
        val arrayList: ArrayList<String> = arrayListOf()
        try {
            val entries = DexFile(context.packageCodePath).entries()
            while (entries.hasMoreElements()) {
                val nextElement = entries.nextElement()
                if (nextElement.contains("com.vega.pay.data")
                    || nextElement.contains("com.vega.cloud.upload.model")
                    || nextElement.contains("com.lemon.cloud.data")
                ) {
                    arrayList.add(nextElement)
                }
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
        return arrayList
    }
}