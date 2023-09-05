package com.ml.xposedproject.hook.auto.impl

import com.google.auto.service.AutoService
import com.ml.xposedproject.hook.auto.AutoHook
import com.ml.xposedproject.hook.ext.hasClass
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

@AutoService(AutoHook::class)
class MimoAds : AutoHook {
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        loadPackageParam.hookAndReplaceMethod("com.miui.zeus.mimo.sdk.MimoSdk", "init", null)
    }

    override fun canHookCurrent(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val hasAds = hasAds(loadPackageParam)
        val src = loadPackageParam.packageName
        log(
            "canHookCurrent can:${hasAds}  source:$src",
            this
        )
        return hasAds
    }

    private fun hasAds(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        return loadPackageParam.hasClass("com.miui.zeus.mimo.sdk.MimoSdk")
    }
}
