package com.ml.xposedproject.hook.auto.impl

import com.google.auto.service.AutoService
import com.ml.xposedproject.hook.auto.AutoHook
import com.ml.xposedproject.hook.ext.findClass
import com.ml.xposedproject.hook.ext.hasClass
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

@AutoService(AutoHook::class)
class GoogleAds : AutoHook {
    private val list = arrayOf(
        "com.google.android.gms.ads.internal.zzk",
        "com.google.android.gms.internal.ads.zzbdl",
        "com.google.android.gms.internal.ads.zzbfk"
    )
    private val nullReplaceList = arrayOf(
        "com.google.android.gms.internal.ads.zzxl",
        "com.google.android.gms.ads.MobileAds",
        "com.google.android.gms.ads.MobileAdsInitProvider"
    )

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        list.forEach {
            loadPackageParam.hookAndReplaceMethod(it, "shouldOverrideUrlLoading", false)
        }
        nullReplaceList.forEach {
            loadPackageParam.findClass(it)?.let {
                it.declaredMethods.forEach {
                    loadPackageParam.hookAndReplaceMethod(it, null)
                }
            }
        }
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
        return nullReplaceList.any {
            loadPackageParam.hasClass(it)
        }
    }
}
