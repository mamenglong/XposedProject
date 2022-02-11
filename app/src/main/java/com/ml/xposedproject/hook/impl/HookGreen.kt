package com.ml.xposedproject.hook.impl

import android.app.Activity
import android.os.Bundle
import android.util.Pair
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.showToast
import de.robv.android.xposed.XC_MethodReplacement
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
class HookGreen : HookPackage {
    override val label: String = "绿巨人"
    override fun getPackage(): String {
        return "us.ljj.vip78"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage:${loadPackageParam.packageName}", this)
        hookUserInfo(loadPackageParam)
        //removeAd(loadPackageParam)
    }

    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookAndReplaceMethod(loadPackageParam,
            "com.hex.wanshiwu.v2.repository.AccountRepo",
            "b",
            Pair(true,"2023-02-11 18:54:27")
            )
        hookAndReplaceMethod(loadPackageParam,
            "com.hex.wanshiwu.util.RouterUtil",
            "f",
            true,
            String::class.java
        )
        hookAndReplaceMethod(loadPackageParam,
            "com.hex.wanshiwu.util.RouterUtil",
            "i",
            true,
        )
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
                        log("SplashAdActivity onCreate ${it?.thisObject}", this@HookGreen)
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