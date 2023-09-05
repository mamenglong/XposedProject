package com.ml.xposedproject.hook.auto.impl

import com.google.auto.service.AutoService
import com.ml.xposedproject.hook.auto.AutoHook
import com.ml.xposedproject.hook.auto.ReplaceMethodItem
import com.ml.xposedproject.hook.ext.hasClass
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

@AutoService(AutoHook::class)
class MBridgeAds : AutoHook {
    private val list = mutableListOf<ReplaceMethodItem>().also {
        it.add(ReplaceMethodItem("com.mbridge.msdk.foundation.entity.CampaignUnit" , "getAdHtml",null))
        it.add(ReplaceMethodItem("com.mbridge.msdk.foundation.entity.CampaignUnit" , "getAdType",0))
        it.add(ReplaceMethodItem("com.mbridge.msdk.foundation.entity.CampaignUnit" , "getAdZip",null))
        it.add(ReplaceMethodItem("com.mbridge.msdk.foundation.entity.CampaignUnit" , "getAds",null))
    }
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        list.forEach {
            loadPackageParam.hookAndReplaceMethod(it.cls, it.method, it.value)
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
        return loadPackageParam.hasClass("com.mbridge.msdk.MBridgeSDK")
    }
}
