package com.ml.xposedproject.hook.active.impl

import android.os.Bundle
import com.google.auto.service.AutoService
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
@AutoService(HookPackage::class)
class HookDy : HookPackage {
    override val label: String = "抖音"
    override fun getPackage(): String {
        return "com.ss.android.ugc.aweme"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage:${loadPackageParam.packageName}", this)
        // hookUserInfo(loadPackageParam)
        //hookLog(loadPackageParam)
        removeAd(loadPackageParam)
    }

    private fun removeAd(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            // hookAndReplaceMethod(loadPackageParam,"com.ss.android.ugc.aweme.splash.SplashActivity","quickLaunch",true)
            findAndHookMethod(
                loadPackageParam,
                "com.ss.android.ugc.aweme.splash.SplashActivity",
                "onCreate",
                registerMethodHookCallback {
                    afterHookedMethod {
                        log("SplashAdActivity onCreate ${it?.thisObject}", this@HookDy)
                        XposedHelpers.setBooleanField(it?.thisObject, "mDirectlyGoMain", true)
                        XposedHelpers.callMethod(it?.thisObject, "finish")
                    }
                },
                Bundle::class.java
            )

        }.onFailure {
            log("onFailure SplashAdActivity onResume :${it}", this)
        }
    }

}