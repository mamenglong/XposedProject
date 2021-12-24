package com.ml.xposedproject

import android.content.Context
import com.ml.xposedproject.hook.HookFactory
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        log("handleLoadPackage ${loadPackageParam.packageName} ${loadPackageParam.processName} ${loadPackageParam.isFirstApplication}", this)
        HookFactory.handleLoadPackage(loadPackageParam)
    }

    override fun initZygote(startupParam: StartupParam) {
        log("$modulePackageName initZygote(${startupParam.startsSystemServer})", this)
    }

    companion object {
        private const val modulePackageName = BuildConfig.APPLICATION_ID
    }
}