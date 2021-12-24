package com.ml.xposedproject.hook.impl

import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2021/5/6 17:25
 * Description: This is ExportHook
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class ExportHook: HookPackage {
    override val label: String = "导出dex"
    override fun getPackage(): String {
        return "com.user.ccnineonepros.android"//context?.let { Config.getValue(it, Config.KEYS.ENABLE_EXPORT,"") }?:""
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        
    }
}