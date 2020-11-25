package com.ml.xposedproject.hook

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
    override fun canHook(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val enable = Config.getBool(Config.KEYS.ENABLE_DY)
        return enable && super.canHook(loadPackageParam)
    }
    override fun getPackage(): String {
        return "com.wechat.yajiji1123"
    }

    override fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookDY(loadPackageParam)
    }
    /**
     * com.wechat.yajiji1123
     */
    private fun hookDY(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.niming.weipa.model.UserInfo2",
                loadPackageParam.classLoader,"getIs_vip", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("hookDY getIs_vip replaceHookedMethod ",this@HookDy)
                        return@replaceHookedMethod true
                    }
                })

        }.onFailure {
            log("hookDY getIs_vip handleLoadPackage:${it.message}",this)
        }
    }
}