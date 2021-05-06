package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import com.ml.xposedproject.log
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2021/5/6 17:25
 * Description: This is ExportHook
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class ExportHook:HookPackage {
    override fun enableHook(): Boolean {
        log(
            "enableHook context:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",
            this
        )
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_EXPORT) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }
    override fun getPackage(): String {
        return "com.user.ccnineonepros.android"//context?.let { Config.getValue(it, Config.KEYS.ENABLE_EXPORT,"") }?:""
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        
    }
}