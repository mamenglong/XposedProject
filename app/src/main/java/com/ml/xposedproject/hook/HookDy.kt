package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.tools.Config
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
class HookDy:HookPackage {
    override fun enableHook(): Boolean {
        log("enableHook context:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",this)
        val enable = context?.let { Config.getBool(it,Config.KEYS.ENABLE_DY) }?:false
        log("enableHook enable:$enable",this)
        return enable
    }

    override fun getPackage(): String {
        return "com.wechat.yajiji1123"
    }
    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage",this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.niming.weipa.model.UserInfo2",
                loadPackageParam.classLoader,"getIs_vip", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("hookDY getIs_vip replaceHookedMethod ",this@HookDy)
                        return@replaceHookedMethod "y"
                    }
                })

        }.onFailure {
            log("hookDY getIs_vip handleLoadPackage:${it.message}",this)
        }
    }
}