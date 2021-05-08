package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import android.os.Build
import android.widget.Toast
import com.ml.xposedproject.MethodHookCallback
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.coroutines.flow.asFlow

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:17
 * Description: This is HookPackage
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
interface HookPackage:BaseHookMethod {
    val context: Context?
        get() = AndroidAppHelper.currentApplication()

    fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val target = getPackage()
        val src = loadPackageParam.packageName
        log("canHook  can:${target == src} target:$target source:$src", this)
        return loadPackageParam.packageName == getPackage()
    }

    fun enableHook(): Boolean
    fun getPackage(): String
    fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookApplication(loadPackageParam)
    }

    fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam)

    /**
     *
     */
    fun hookApplication(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("android.app.Application",
                loadPackageParam.classLoader, "onCreate", registerMethodHookCallback {
                    afterHookedMethod {
                        val app = it!!.thisObject as Context
                        if (enableHook()) {
                            hookPackage(loadPackageParam)
                        }

                        Toast.makeText(
                            app,
                            "hookEnable:${enableHook()} ${loadPackageParam.appInfo.loadLabel(app.packageManager)}:${loadPackageParam.packageName} ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }.onFailure {
            log("hook onCreate onFailure:${it.message}", this)
        }

    }


}