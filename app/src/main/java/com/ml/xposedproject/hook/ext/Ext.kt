package com.ml.xposedproject.hook.ext

import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Method

/**
 * load
 */
fun XC_LoadPackage.LoadPackageParam.findClass(
    cls: String,
    classLoader: ClassLoader = this.classLoader
): Class<*>? {
    return runCatching {
        XposedHelpers.findClass(cls, classLoader)
    }.fold(
        onSuccess = {
            it
        }, onFailure = {
            null
        }
    )
}


fun XC_LoadPackage.LoadPackageParam.hasClass(
    cls: String,
    classLoader: ClassLoader = this.classLoader
): Boolean {
    return findClass(cls) != null
}
fun XC_LoadPackage.LoadPackageParam.hookPrintClassMethod(
    cls: String,
    classLoader: ClassLoader = this.classLoader
) {
    findClass(cls,classLoader)?.declaredMethods?.forEach {
        log("hookPrintClassMethod:${cls},method:$it",this)
    }
}

/**
 * 基础替换方法
 * @param newValue null 不替换
 */
fun XC_LoadPackage.LoadPackageParam.hookAndReplaceMethod(
    className: String,
    methodName: String,
    newValue: Any? = null,
    vararg params: Any
) {
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(className,
            classLoader,
            methodName,
            *params,
            registerMethodReplaceHookCallback {
                replaceHookedMethod {
                    log(
                        "hookAndReplaceMethod->$className.$methodName  old:${it?.result} new:$newValue",
                        this@hookAndReplaceMethod
                    )
                    return@replaceHookedMethod newValue
                }
            })
        log(
            "hookAndReplaceMethod onSuccess $className.$methodName",
            this@hookAndReplaceMethod
        )
    }.onFailure {
        log(
            "hookAndReplaceMethod onFailure $className.$methodName ->:${it.message}",
            this@hookAndReplaceMethod
        )
    }
}

/**
 * 根据条件替换方法返回值
 * @param condition 条件
 *
 */
fun <T> XC_LoadPackage.LoadPackageParam.hookAndReplaceMethodByCondition(
    className: String,
    methodName: String,
    newValue: T?,
    condition: (oldValue: T?, params: Array<out Any>?) -> Boolean,
    vararg params: Any
) {
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(className,
            classLoader,
            methodName,
            *params,
            registerMethodReplaceHookCallback {
                replaceHookedMethod {
                    val oldValue = it?.result as? T
                    val p = it?.args
                    val oldParam =
                        buildString {
                            params.forEachIndexed { index, any ->
                                append("${it?.args?.get(index)}, ")
                            }
                        }
                    val hook = condition.invoke(oldValue, p)
                    val result = when {
                        hook -> newValue
                        else -> it?.result
                    }
                    val cc = if (hook) "newValue:${newValue}" else "newValue:null"
                    log(
                        "hookAndReplaceMethodByConditionExec->$className.$methodName[${it?.args?.size}]($oldParam)\n" +
                                "hook:$hook oldValue:$oldValue $cc result:$result",
                        this@hookAndReplaceMethodByCondition
                    )
                    return@replaceHookedMethod result
                }
            })
        log(
            "hookAndReplaceMethodByCondition onSuccess $className.$methodName",
            this@hookAndReplaceMethodByCondition
        )
    }.onFailure {
        log(
            "hookAndReplaceMethodByCondition onFailure $className.$methodName ->:${it}",
            this@hookAndReplaceMethodByCondition
        )
    }
}

/**
 * hook方法并打印参数和结果
 */

fun XC_LoadPackage.LoadPackageParam.hookMethodAndPrint(
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
    log("hookMethodAndPrint $className.$methodName(${p}) ", this)
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(className,
            classLoader, methodName, *params, registerMethodHookCallback {
                afterHookedMethod {
                    val oldParam = buildString {
                        params.forEachIndexed { index, any ->
                            append("${it?.args?.get(index)}, ")
                        }
                    }
                    log(
                        "hookMethodAndPrintExec $className.$methodName[${it?.args?.size}]($oldParam) return:${it?.result}",
                        this@hookMethodAndPrint
                    )
                }
            })
        log("hookMethodAndPrint onSuccess $className.$methodName", this)
    }.onFailure {
        log(
            "hookMethodAndPrint onFailure $className.$methodName ->:${it}",
            this
        )
    }

}

/**
 * 查找并hook方法
 */
fun XC_LoadPackage.LoadPackageParam.findAndHookMethod(
    className: String,
    methodName: String,
    callback: XC_MethodHook,
    vararg params: Any
) {
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(
            className,
            classLoader, methodName, *params, callback
        )
        log("findAndHookMethod onSuccess $className.$methodName", this)
    }.onFailure {
        log(
            "findAndHookMethod onFailure $className.$methodName ->:${it.message}",
            this
        )
    }
}

fun XC_LoadPackage.LoadPackageParam.hookAndReplaceMethod(method: Method, newValue: Any?) {
    runCatching {
        XposedBridge.hookMethod(method, registerMethodReplaceHookCallback {
            replaceHookedMethod {
                return@replaceHookedMethod newValue
            }
        })
        log(
            "hookAndReplaceMethod onSuccess ${method.declaringClass.name}.${method.name}",
            this
        )
    }.onFailure {
        log(
            "hookAndReplaceMethod onFailure ${method.declaringClass.name}.${method.name} ->:${it.message}",
            this
        )
    }
}

fun XC_LoadPackage.LoadPackageParam.setObjectField(
    className: String,
    filedName: String,
    newValue: Any?,
    isStatic: Boolean = false,
    vararg params: Any
) {
    kotlin.runCatching {
        val clazz = XposedHelpers.findClass(className, classLoader)
        var oldValue: Any? = Any()
        if (isStatic) {
            oldValue = XposedHelpers.getStaticObjectField(clazz, filedName)
            XposedHelpers.setStaticObjectField(clazz, filedName, newValue)
            log(
                "setObjectField onSuccess $className.$filedName ->old:$oldValue new:$newValue",
                this
            )
        } else {
            XposedHelpers.findAndHookConstructor(clazz, *params, registerMethodHookCallback {
                afterHookedMethod {
                    log(
                        "setObjectField $className.$filedName -> method:${it!!.method.name}",
                        this@setObjectField
                    )
                    oldValue = XposedHelpers.getObjectField(it.thisObject, filedName)
                    XposedHelpers.setObjectField(it.thisObject, filedName, newValue)
                    val newV = XposedHelpers.getObjectField(it.thisObject, filedName)
                    log(
                        "setObjectField onSuccess $className.$filedName ->old:$oldValue new:$newV",
                        this@setObjectField
                    )
                }
            })
        }

    }.onFailure {
        log(
            "setObjectField onFailure $className.$filedName ->:${it}",
            this
        )
    }

}

/**
 * hook 参数
 */
fun XC_LoadPackage.LoadPackageParam.hookMethodParams(
    className: String,
    methodName: String,
    newValue: Any?,
    index: Int,
    vararg params: Any
) {
    val p = params.joinToString {
        when (it) {
            is String -> it
            is Class<*> -> it.name
            else -> "$it"
        }
    }
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(className,
            classLoader, methodName, *params, registerMethodHookCallback {
                beforeHookedMethod {
                    val oldParam = buildString {
                        params.forEachIndexed { index, any ->
                            append("${it?.args?.get(index)}, ")
                        }
                    }
                    it?.args?.set(index, newValue)
                    log(
                        "hookMethodParams $className.$methodName[${it?.args?.size}]($oldParam)[$index]($newValue) return:${it?.result}",
                        this@hookMethodParams
                    )
                }
            })
        log("hookMethodParams onSuccess $className.$methodName", this)
    }.onFailure {
        log(
            "hookMethodParams onFailure $className.$methodName ->:${it}",
            this
        )
    }


}

/**
 * hook 参数
 */
fun XC_LoadPackage.LoadPackageParam.hookMethodParams(
    className: String,
    methodName: String,
    newValues: List<Any?>,
    vararg params: Any
) {
    val p = params.joinToString {
        when (it) {
            is String -> it
            is Class<*> -> it.name
            else -> "$it"
        }
    }
    kotlin.runCatching {
        XposedHelpers.findAndHookMethod(className,
            classLoader, methodName, *params, registerMethodHookCallback {
                beforeHookedMethod {
                    val oldParam = buildString {
                        params.forEachIndexed { index, any ->
                            append("${it?.args?.get(index)}, ")
                        }
                    }
                    newValues.forEachIndexed { index, any ->
                        any?.apply {
                            it?.args?.set(index, any)
                        }
                    }
                    val newParam = buildString {
                        params.forEachIndexed { index, any ->
                            append("${it?.args?.get(index)}, ")
                        }
                    }
                    log(
                        "hookMethodParams $className.$methodName[${it?.args?.size}]($oldParam)($newParam) return:${it?.result}",
                        this@hookMethodParams
                    )
                }
            })
        log("hookMethodParams onSuccess $className.$methodName", this)
    }.onFailure {
        log(
            "hookMethodParams onFailure $className.$methodName ->:${it}",
            this
        )
    }


}