package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import com.ml.xposedproject.*
import com.ml.xposedproject.test.TestFiled
import com.ml.xposedproject.test.TestObject
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook.MethodHookParam

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
    override fun enableHook(): Boolean {
        log(
            "enableHook context:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",
            this
        )
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_O_P_M_A) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }

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