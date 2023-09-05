package com.ml.xposedproject.hook.active.impl

import android.content.Context
import com.google.auto.service.AutoService
import com.google.gson.JsonObject
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.hook.active.base.PackageWithConfig
import com.ml.xposedproject.hook.ext.findClass
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import com.ml.xposedproject.showToast
import dalvik.system.DexFile
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.Boolean
import kotlin.String
import kotlin.Throwable

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
@AutoService(HookPackage::class)
class HookXT : PackageWithConfig() {
    override val label: String = "醒图"
    override fun getPackage(): String {
        return "com.xt.retouch"
    }

    override fun exec(loadPackageParam: LoadPackageParam,jsonObject: JsonObject) {
        val cls = jsonObject.get("class").asString
        loadPackageParam.hookAndReplaceMethod(cls, "f", true)
        loadPackageParam.hookAndReplaceMethod(cls, "a", true)
        showToast("适配完成.")
    }
    override fun init(loadPackageParam: LoadPackageParam) {
        for (str in getClassNames(context!!)) {
            for (field in loadPackageParam.findClass(str)!!.declaredFields) {
                if (field.type == Boolean.TYPE) {
                    for (annotation in field.annotations) {
                        try {
                            if (annotation.toString().contains("flag")) {
                                val declaringClass = field.declaringClass
                                log(
                                    "init class name:${declaringClass.name},annotation:${annotation.toString()}",
                                    this
                                )
                                loadPackageParam.hookAndReplaceMethod(
                                    declaringClass.name,
                                    "a",
                                    true
                                )
                                loadPackageParam.hookAndReplaceMethod(
                                    declaringClass.name,
                                    "f",
                                    true
                                )
                                saveConfig() {
                                    it.put("class", declaringClass.name)
                                }
                                showToast("适配完成...")
                            }
                        } catch (th: Throwable) {
                            log("init error:${th.message}", this)
                        }
                    }
                }
            }
        }
    }

    private fun getClassNames(context: Context): List<String> {
        val arrayList: ArrayList<String> = arrayListOf()
        try {
            val entries = DexFile(context.packageCodePath).entries()
            while (entries.hasMoreElements()) {
                val nextElement = entries.nextElement()
                if (nextElement.contains("com.xt.retouch.subscribe.api")) {
                    arrayList.add(nextElement)
                }
            }
        } catch (th: Throwable) {
            th.printStackTrace()
        }
        return arrayList
    }
}