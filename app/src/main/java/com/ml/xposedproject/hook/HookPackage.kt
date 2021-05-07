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
interface HookPackage {
    val context: Context?
        get() = AndroidAppHelper.currentApplication()

    fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val target = getPackage()
        val src = loadPackageParam.packageName
        log("canHook target:$target source:$src can:${target == src}", this)
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
                        if (enableHook())
                            hookPackage(loadPackageParam)

                        Toast.makeText(
                            app,
                            "hook onCreate:${loadPackageParam.appInfo.loadLabel(app.packageManager)}:${loadPackageParam.packageName} ",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })

        }.onFailure {
            log("hook onCreate onFailure:${it.message}", this)
        }

    }


    fun hookAndReplaceMethod(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        newValue: Any?,
        vararg params: Any
    ) {
        XposedHelpers.findAndHookMethod(className,
            loadPackageParam.classLoader, methodName, *params, registerMethodReplaceHookCallback {
                replaceHookedMethod {
                    log("replace->$className.$methodName", this@HookPackage)
                    return@replaceHookedMethod newValue
                }
            })
    }

    fun hookAndReplaceMethodAndPrintResult(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        newValue: Any?,
        vararg params: Array<out Any>
    ) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader, methodName, *params, registerMethodHookCallback {
                    afterHookedMethod {
                        val old = "${it?.result}"
                        it?.result = newValue
                        log("$className.$methodName old:$old new:${it?.result} ", this@HookPackage)
                    }
                })
            log(
                "hookAndReplaceMethodPrintOrigin onSuccess $className.$methodName",
                this@HookPackage
            )
        }.onFailure {
            log(
                "hookAndReplaceMethodPrintOrigin onFailure $className.$methodName ->:${it.message}",
                this@HookPackage
            )
        }
    }

    fun hookMethodAndPrintParams(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        vararg params: Any
    ) {
        val p = params.joinToString {
            when (it) {
                is String -> it
                is Class<*> -> it.name
                else -> "$it"
            }
        }
        log("hookMethodAndPrintParams $className.$methodName params :${p} ", this@HookPackage)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader, methodName, *params, registerMethodHookCallback {
                    afterHookedMethod {
                        var oldParam = ""
                        params.forEachIndexed { index, any ->
                            oldParam += " ${it?.args?.get(index)} "
                        }
                        log(
                            "hookMethodAndPrintParams $className.$methodName params :${oldParam} ",
                            this@HookPackage
                        )
                    }
                })
            log("hookMethodAndPrintParams onSuccess $className.$methodName", this@HookPackage)
        }.onFailure {
            log(
                "hookMethodAndPrintParams onFailure $className.$methodName ->:${it.message}",
                this@HookPackage
            )
        }


    }

    fun findAndHookMethod(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        callback: XC_MethodHook,
        vararg params: Any
    ) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader, methodName, *params, callback)
            log("findAndHookMethod onSuccess $className.$methodName", this@HookPackage)
        }.onFailure {
            log(
                "findAndHookMethod onFailure $className.$methodName ->:${it.message}",
                this@HookPackage
            )
        }
    }
}