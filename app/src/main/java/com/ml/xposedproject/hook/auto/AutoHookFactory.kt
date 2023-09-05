package com.ml.xposedproject.hook.auto

import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.ServiceLoader

object AutoHookFactory {
    private val hookList = mutableListOf<AutoHook>()

    init {
        ServiceLoader.load(AutoHook::class.java, AutoHookFactory::class.java.classLoader).let {
            it.forEach {
                hookList.add(it)
            }
        }
    }

    fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage ${hookList.size}",this)
        hookList.forEach {
            it.handleLoadPackage(loadPackageParam)
        }
    }
}