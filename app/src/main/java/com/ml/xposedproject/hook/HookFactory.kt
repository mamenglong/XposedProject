package com.ml.xposedproject.hook

import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:24
 * Description: This is HookFactory
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
object HookFactory {
    private val hookList = mutableListOf<HookPackage>()
    init {
        hookList.add(HookSelf())
        hookList.add(HookXYJMH())
        hookList.add(HookDy())
    }
    fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        log("doHook",this)
        hookList.forEach {
            if (it.canHook(loadPackageParam))
                it.doHook(loadPackageParam)
        }
    }
}