package com.ml.xposedproject.hook.active.impl

import android.content.Context
import com.google.auto.service.AutoService
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.active.base.HookPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
@AutoService(HookPackage::class)
class HookOnePlusMultiApp : HookPackage {
    override val label: String = "一加多开"
    override fun getPackage(): String {
        return "com.android.settings"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            hookAndReplaceMethod(loadPackageParam,"com.oneplus.settings.apploader.OPApplicationLoader","multiAppPackageExcludeFilter",
                true,
                Context::class.java,
                String::class.java,
                )
        }.onFailure {
            log("hookCurrentPackage onFailure:${it.message}", this)
        }

    }
}