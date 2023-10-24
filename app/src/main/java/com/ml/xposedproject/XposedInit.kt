package com.ml.xposedproject

import com.ml.xposedproject.hook.auto.AutoHookFactory
import com.ml.xposedproject.hook.active.HookFactory
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        log("handleLoadPackage ${loadPackageParam.packageName} ${loadPackageParam.processName} ${loadPackageParam.isFirstApplication}", this)
        HookFactory.handleLoadPackage(loadPackageParam)
        AutoHookFactory.handleLoadPackage(loadPackageParam)
    }

    override fun initZygote(startupParam: StartupParam) {
        log("$modulePackageName initZygote(${startupParam.startsSystemServer})", this)
    }

    companion object {
        private const val modulePackageName = BuildConfig.APPLICATION_ID
        init {
            System.loadLibrary("dexkit")
        }
    }
}