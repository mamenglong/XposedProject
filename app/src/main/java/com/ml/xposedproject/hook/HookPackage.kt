package com.ml.xposedproject.hook

import com.ml.xposedproject.tools.Config
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
    fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam):Boolean{
        return loadPackageParam.packageName == getPackage()
    }
    fun getPackage():String
    fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam)
}