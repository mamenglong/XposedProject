package com.ml.xposedproject.hook.base

import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import de.robv.android.xposed.IXposedHookLoadPackage
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

    /**
     * 基础替换方法
     * @param newValue null 不替换
     */
    fun hookAndReplaceMethod(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        newValue: Any? = null,
        vararg params: Any
    ) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader,
                methodName,
                *params,
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log(
                            "hookAndReplaceMethod->$className.$methodName  old:${it?.result} new:$newValue",
                            this@BaseHookMethod
                        )
                        return@replaceHookedMethod newValue
                    }
                })
            log(
                "hookAndReplaceMethod onSuccess $className.$methodName",
                this@BaseHookMethod
            )
        }.onFailure {
            log(
                "hookAndReplaceMethod onFailure $className.$methodName ->:${it.message}",
                this@BaseHookMethod
            )
        }
    }

    /**
     * 根据条件替换方法返回值
     * @param condition 条件
     *
     */
    fun <T> hookAndReplaceMethodByCondition(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        newValue: T?,
        condition: (oldValue:T?,params:Array<out Any>?) -> Boolean,
        vararg params: Any
    ) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader,
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
                        val hook = condition.invoke(oldValue,p)
                        val result = when {
                            hook -> newValue
                            else -> it?.result
                        }
                        val cc = if (hook) "newValue:${newValue}" else "newValue:null"
                        log(
                            "hookAndReplaceMethodByConditionExec->$className.$methodName[${it?.args?.size}]($oldParam)\n" +
                                    "hook:$hook oldValue:$oldValue $cc result:$result",
                            this@BaseHookMethod
                        )
                        return@replaceHookedMethod result
                    }
                })
            log("hookAndReplaceMethodByCondition onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "hookAndReplaceMethodByCondition onFailure $className.$methodName ->:${it}",
                this@BaseHookMethod
            )
        }
    }

    /**
     * hook方法并打印参数和结果
     */

    fun hookMethodAndPrint(
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
        log("hookMethodAndPrint $className.$methodName(${p}) ", this@BaseHookMethod)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(className,
                loadPackageParam.classLoader, methodName, *params, registerMethodHookCallback {
                    afterHookedMethod {
                        val oldParam = buildString {
                            params.forEachIndexed { index, any ->
                                append("${it?.args?.get(index)}, ")
                            }
                        }
                        log(
                            "hookMethodAndPrintExec $className.$methodName[${it?.args?.size}]($oldParam) return:${it?.result}",
                            this@BaseHookMethod
                        )
                    }
                })
            log("hookMethodAndPrint onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "hookMethodAndPrint onFailure $className.$methodName ->:${it}",
                this@BaseHookMethod
            )
        }


    }

    /**
     * 查找并hook方法
     */
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
        vararg params: Any
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
            } else {
                XposedHelpers.findAndHookConstructor(clazz, *params, registerMethodHookCallback {
                    afterHookedMethod {
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

    /**
     * hook 参数
     */
    fun hookMethodParams(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String,
        methodName: String,
        newValue: Any?,
        index:Int,
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
                loadPackageParam.classLoader, methodName, *params, registerMethodHookCallback {
                    beforeHookedMethod {
                        val oldParam = buildString {
                            params.forEachIndexed { index, any ->
                                append("${it?.args?.get(index)}, ")
                            }
                        }
                        it?.args?.set(index, newValue)
                        log(
                            "hookMethodParams $className.$methodName[${it?.args?.size}]($oldParam)[$index]($newValue) return:${it?.result}",
                            this@BaseHookMethod
                        )
                    }
                })
            log("hookMethodParams onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "hookMethodParams onFailure $className.$methodName ->:${it}",
                this@BaseHookMethod
            )
        }


    }
    /**
     * hook 参数
     */
    fun hookMethodParams(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
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
                loadPackageParam.classLoader, methodName, *params, registerMethodHookCallback {
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
                            this@BaseHookMethod
                        )
                    }
                })
            log("hookMethodParams onSuccess $className.$methodName", this@BaseHookMethod)
        }.onFailure {
            log(
                "hookMethodParams onFailure $className.$methodName ->:${it}",
                this@BaseHookMethod
            )
        }


    }
}