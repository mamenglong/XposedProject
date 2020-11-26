package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import android.widget.Toast
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.tools.Config
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

                        Toast.makeText(app,"hook onCreate ",Toast.LENGTH_SHORT).show()
                    }
                })

        }.onFailure {
            log("hook onCreate onFailure:${it.message}", this)
        }

    }
}