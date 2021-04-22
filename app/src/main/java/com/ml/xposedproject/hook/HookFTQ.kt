package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookFTQ : HookPackage {
    override fun getPackage(): String {
        return "com.fotiaoqiang.android.vpn"
    }

    override fun enableHook(): Boolean {
        log("enableHook context:${AndroidAppHelper.currentPackageName()}  context:${AndroidAppHelper.currentApplication().packageName}",this)
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_FTQ) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }
    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage $loadPackageParam", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader, "isVip", registerMethodHookCallback {
                    afterHookedMethod {
                        log("hookXYJMH isVip afterHookedMethod origin value:${it?.result}", this@HookFTQ)
                        it?.result = true
                        log("hookXYJMH isVip afterHookedMethod  new   value:${it?.result}", this@HookFTQ)
                    }
                })

        }.onFailure {
            log("isVip handleLoadPackage:${it.message}", this)
        }
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader, "getWx_openid", registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("hookXYJMH getWx_openid  replaceHookedMethod ", this@HookFTQ)
                        return@replaceHookedMethod "true"
                    }
                })

        }.onFailure {
            log("getWx_openid handleLoadPackage :${it.message}", this)
        }
    }
}