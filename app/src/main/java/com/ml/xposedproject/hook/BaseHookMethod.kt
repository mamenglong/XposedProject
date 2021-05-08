package com.ml.xposedproject.hook

import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2021/5/8 09:37
 * Description: This is BaseHookMethod
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
interface BaseHookMethod {

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
                    log("replace->$className.$methodName", this@BaseHookMethod)
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
                        log(
                            "$className.$methodName old:$old new:${it?.result} ",
                            this@BaseHookMethod
                        )
                    }
                })
            log(
                "hookAndReplaceMethodPrintOrigin onSuccess $className.$methodName",
                this@BaseHookMethod
            )
        }.onFailure {
            log(
                "hookAndReplaceMethodPrintOrigin onFailure $className.$methodName ->:${it.message}",
                this@BaseHookMethod
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
        log("hookMethodAndPrintParams $className.$methodName params :${p} ", this@BaseHookMethod)
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
                            this@BaseHookMethod
                        )
                    }
                })
            log("hookMethodAndPrintParams onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "hookMethodAndPrintParams onFailure $className.$methodName ->:${it.message}",
                this@BaseHookMethod
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
            XposedHelpers.findAndHookMethod(
                className,
                loadPackageParam.classLoader, methodName, *params, callback
            )
            log("findAndHookMethod onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "findAndHookMethod onFailure $className.$methodName ->:${it.message}",
                this@BaseHookMethod
            )
        }
    }

    fun setObjectField(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        filedName: String,
        newValue: Any?,
        isStatic: Boolean = false,
        vararg params: Array<out Any>
    ) {
        kotlin.runCatching {
            val clazz = XposedHelpers.findClass(className, loadPackageParam.classLoader)
            var oldValue: Any? = Any()
            if (isStatic) {
                oldValue = XposedHelpers.getStaticObjectField(clazz, filedName)
                XposedHelpers.setStaticObjectField(clazz, filedName, newValue)
                log(
                    "setObjectField onSuccess $className.$filedName ->old:$oldValue new:$newValue",
                    this@BaseHookMethod
                )
            }else {
                XposedHelpers.findAndHookConstructor(clazz,*params, registerMethodHookCallback {
                    afterHookedMethod{
                        log(
                            "setObjectField $className.$filedName -> method:${it!!.method.name}",
                            this@BaseHookMethod
                        )
                        oldValue = XposedHelpers.getObjectField(it.thisObject, filedName)
                        XposedHelpers.setObjectField(it.thisObject, filedName, newValue)
                        val newV = XposedHelpers.getObjectField(it.thisObject, filedName)
                        log(
                            "setObjectField onSuccess $className.$filedName ->old:$oldValue new:$newV",
                            this@BaseHookMethod
                        )
                    }
                })
            }

        }.onFailure {
            log(
                "setObjectField onFailure $className.$filedName ->:${it}",
                this@BaseHookMethod
            )
        }


    }
}