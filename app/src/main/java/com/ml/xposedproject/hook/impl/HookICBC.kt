package com.ml.xposedproject.hook.impl

import android.content.Context
import android.content.pm.PackageInfo
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.ArrayList

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookICBC : HookPackage {
    override val label: String = "ICBC"
    override fun getPackage(): String {
        return "com.icbc"
    }
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        val a= arrayListOf<String>()
        kotlin.runCatching {
            hookAndReplaceMethod(
                loadPackageParam, "com.utils.safeUtils",
                "haveHookPlugin",
                arrayListOf<String>(),
                Context::class.java,
                a::class.java
            )
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
}