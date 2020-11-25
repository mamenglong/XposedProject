package com.ml.xposedproject.hook

import com.ml.xposedproject.BuildConfig
import com.ml.xposedproject.MainActivity
import com.ml.xposedproject.log
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
class HookSelf:HookPackage {
    override fun getPackage(): String {
         return  BuildConfig.APPLICATION_ID
    }

    override fun doHook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookSelf(loadPackageParam)
    }

    private fun hookSelf(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "getHook", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("replaceHookedMethod $it",this@HookSelf)
                        return@replaceHookedMethod "Hook succeed"
                    }
                })

        }.onFailure {
            log("getHook handleLoadPackage:${it.message}",this)
        }

    }
}