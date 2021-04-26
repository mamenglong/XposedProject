package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import android.widget.Toast
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:17
 * Description: This is HookPackage
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
interface HookPackage {
    val context:Context?
    get() = AndroidAppHelper.currentApplication()
    fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam):Boolean{
        return loadPackageParam.packageName == getPackage()
    }
    fun enableHook():Boolean
    fun getPackage():String
    fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        hookApplication(loadPackageParam)
    }
    fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam)

    /**
     * 
     */
    fun hookApplication(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("android.app.Application",
                loadPackageParam.classLoader, "onCreate", registerMethodHookCallback {
                    afterHookedMethod {
                        val app = it!!.thisObject as Context
                        if (enableHook())
                            hookPackage(loadPackageParam)

                        Toast.makeText(app,"hook onCreate:${loadPackageParam.packageName} ",Toast.LENGTH_SHORT).show()
                    }
                })

        }.onFailure {
            log("hook onCreate onFailure:${it.message}", this)
        }

    }


    fun hookAndReplaceMethod(loadPackageParam: XC_LoadPackage.LoadPackageParam, className:String, methodName: String, newValue: Any?){
        XposedHelpers.findAndHookMethod(className,
            loadPackageParam.classLoader, methodName, registerMethodReplaceHookCallback {
                replaceHookedMethod {
                    log("replace->$className.$methodName", this@HookPackage)
                    return@replaceHookedMethod newValue
                }
            })
    }
    fun hookAndReplaceMethodPrintOrigin(loadPackageParam: XC_LoadPackage.LoadPackageParam,className:String,methodName: String, newValue: Any?){
        XposedHelpers.findAndHookMethod(className,
            loadPackageParam.classLoader, methodName, registerMethodHookCallback {
                afterHookedMethod {
                    log("$className.$methodName old:${it?.result} ", this@HookPackage)
                    newValue?.let { value->
                        it?.result = value
                        log("$className.$methodName new:${it?.result} ", this@HookPackage)
                    }
                }
            })
    }

    fun hookMethodPrint(loadPackageParam: XC_LoadPackage.LoadPackageParam,className:String,methodName: String,vararg params:Class<*>){

        XposedHelpers.findAndHookMethod(className,
            loadPackageParam.classLoader, methodName,params, registerMethodHookCallback {
                afterHookedMethod{
                    var oldParam = ""
                    params.forEach {p->
                        oldParam+=" ${it?.args?.get(0)} "
                    }
                    log("$className.$methodName params :${oldParam} ", this@HookPackage)
                }
            })
    }
}