package com.ml.xposedproject.hook

import com.ml.xposedproject.log
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
class HookXYJMH:HookPackage {
    override fun getPackage(): String {
         return "com.xjlmh.classic"
    }

    override fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val enable = Config.getBool(Config.KEYS.ENABLE_XYJMH)
        return enable && super.canHook(loadPackageParam)
    }
    override fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookXYJMH(loadPackageParam)
    }

    private fun hookXYJMH(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader,"isVip", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("hookXYJMH isVip replaceHookedMethod ",this@HookXYJMH)
                        return@replaceHookedMethod true
                    }
                })

        }.onFailure {
            log("isVip handleLoadPackage:${it.message}",this)
        }
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
                loadPackageParam.classLoader,"getWx_openid", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("hookXYJMH getWx_openid  replaceHookedMethod ",this@HookXYJMH)
                        return@replaceHookedMethod "true"
                    }
                })

        }.onFailure {
            log("getWx_openid handleLoadPackage :${it.message}",this)
        }
    }
}