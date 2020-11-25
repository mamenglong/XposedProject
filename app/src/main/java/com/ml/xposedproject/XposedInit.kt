package com.ml.xposedproject

import android.content.Context
import com.ml.xposedproject.hook.HookFactory
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        log("$modulePackageName handleLoadPackage", this)
        HookFactory.doHook(loadPackageParam)
    }

    override fun initZygote(startupParam: StartupParam) {
        log("$modulePackageName initZygote", this)
    }

    companion object {
        private const val modulePackageName = BuildConfig.APPLICATION_ID
    }
}