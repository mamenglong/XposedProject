package com.ml.xposedproject.hook.impl

import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
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
class HookXYJMH : HookPackage {
    override val label: String = "小妖精美化"
    override fun getPackage(): String {
        return "com.xjlmh.classic"
    }
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage $loadPackageParam", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader, "isVip", registerMethodHookCallback {
                    afterHookedMethod {
                        log("hookXYJMH isVip afterHookedMethod origin value:${it?.result}", this@HookXYJMH)
                        it?.result = true
                        log("hookXYJMH isVip afterHookedMethod  new   value:${it?.result}", this@HookXYJMH)
                    }
                })

        }.onFailure {
            log("isVip handleLoadPackage:${it.message}", this)
        }
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader, "getWx_openid", registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("hookXYJMH getWx_openid  replaceHookedMethod ", this@HookXYJMH)
                        return@replaceHookedMethod "true"
                    }
                })

        }.onFailure {
            log("getWx_openid handleLoadPackage :${it.message}", this)
        }
    }
}