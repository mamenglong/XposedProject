package com.ml.xposedproject.hook.impl

import android.content.Context
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

import de.robv.android.xposed.XC_MethodHook




/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookOnePlusMultiApp : HookPackage {
    override val label: String = "一加多开"
    override fun getPackage(): String {
        return "com.android.settings"
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(loadPackageParam.classLoader.loadClass("com.oneplus.settings.apploader.OPApplicationLoader"),
                "multiAppPackageExcludeFilter",
                Context::class.java,
                String::class.java,
                object : XC_MethodHook() {
                    @Throws(Throwable::class)
                    override fun afterHookedMethod(param: MethodHookParam) {
                        param.result = true
                    }
                })
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
}