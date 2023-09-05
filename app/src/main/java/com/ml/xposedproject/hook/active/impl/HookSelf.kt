package com.ml.xposedproject.hook.active.impl

import com.google.auto.service.AutoService
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.test.TestFiled
import com.ml.xposedproject.test.TestObject
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
@AutoService(HookPackage::class)
class HookSelf : HookPackage {
    override val label: String = "self"
    override fun isEnableCurrentPackageHook(): Boolean {
        return true
    }

    override fun getPackage(): String {
        return BuildConfig.APPLICATION_ID
    }
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "isActive", registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("${it?.method}",this@HookSelf)
                        return@replaceHookedMethod true
                    }
                })
            this.hookMethodAndPrint(loadPackageParam,TestObject::class.java.name,"testHook",String::class.java)
            setObjectField(loadPackageParam,TestObject::class.java.name,"testFiled","TestObject.class",true)
            setObjectField(loadPackageParam,TestFiled::class.java.name,"testFiled","TestFiled.class")
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
}