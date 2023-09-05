package com.ml.xposedproject.hook.auto

import android.app.AndroidAppHelper
import android.content.Context
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

interface AutoHook {
    val context: Context?
        get() = AndroidAppHelper.currentApplication()

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage", this)
        hookApplication(loadPackageParam = lpparam)
    }

    private fun hookApplication(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(
                "android.app.Application",
                loadPackageParam.classLoader,
                "attach",
                Context::class.java,
                registerMethodHookCallback {
                    afterHookedMethod {
                        val app = it!!.thisObject as Context
                        val ctx = it.args.get(0) as Context
                        val label = loadPackageParam.appInfo.loadLabel(ctx.packageManager)
                        log("hook attach label:${label}", this)
                        if (canHookCurrent(loadPackageParam)) {
                            hookCurrentPackage(loadPackageParam)
                        }
                    }
                }
            )
        }.onFailure {
            log("hook attach onFailure:${it.message}", this)
        }

    }

    fun canHookCurrent(loadPackageParam: LoadPackageParam): Boolean
    fun hookCurrentPackage(loadPackageParam: LoadPackageParam)

}