package com.ml.xposedproject.hook

import com.ml.xposedproject.*
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookSelf : HookPackage {
    override fun enableHook(): Boolean {
        return true
    }

    override fun getPackage(): String {
        return BuildConfig.APPLICATION_ID
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage $loadPackageParam", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "getHook", registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("${it?.method}",this@HookSelf)
                        return@replaceHookedMethod "模块已启用"
                    }
                })
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "isActive", registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("${it?.method}",this@HookSelf)
                        return@replaceHookedMethod true
                    }
                })
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "onResume", registerMethodHookCallback {
                    afterHookedMethod {
                        log("${it?.method}",this@HookSelf)
                        XposedHelpers.callMethod(it?.thisObject, "hookMe", "hahahh")
                        it?.result  = null
                    }
                })
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
}