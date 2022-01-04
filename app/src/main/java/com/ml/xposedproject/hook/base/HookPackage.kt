package com.ml.xposedproject.hook.base

import android.app.AndroidAppHelper
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.ml.xposedproject.BuildConfig
import com.ml.xposedproject.log
import com.ml.xposedproject.provider.ConfigContentProvider
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.service.AliveActivity
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:17
 * Description: This is HookPackage
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
interface HookPackage : BaseHookMethod {
    val context: Context?
        get() = AndroidAppHelper.currentApplication()

    val label: String
    val key: String
        get() = this.javaClass.simpleName

    fun isCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam): Boolean {
        val target = getPackage()
        val src = loadPackageParam.packageName
        log("isCurrentPackage is:${target == src} target:$target source:$src", this)
        return loadPackageParam.packageName == getPackage()
    }

    fun isEnableCurrentPackageHook(): Boolean {
        log(
            "isEnableCurrentPackageHook key:$key current:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",
            this
        )
        val enable = context?.let { Config.getBool(it, key) } ?: false
        log("isEnableCurrentPackageHook key:$key enable:$enable", this)
        return enable
    }

    fun getPackage(): String
    fun execHook(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        hookApplication(loadPackageParam)
    }

    fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam)

    /**
     *
     */
    private fun hookApplication(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("android.app.Application",
                loadPackageParam.classLoader, "onCreate", registerMethodHookCallback {
                    afterHookedMethod {
                        val app = it!!.thisObject as Context
                        val label = loadPackageParam.appInfo.loadLabel(app.packageManager)
                        log("hookApplication($label) processName:${loadPackageParam.processName}" +
                                " isFirstApplication:${loadPackageParam.isFirstApplication}",this@HookPackage)
                      /*  if(ConfigContentProvider.isEnable.not()){
                            val intent = Intent()
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            intent.setComponent(ComponentName(BuildConfig.APPLICATION_ID,
                                AliveActivity::class.java.name))
                            app.startActivity(intent)
                        }*/
                        if (loadPackageParam.isFirstApplication&&loadPackageParam.processName==getPackage()) {
                            Toast.makeText(
                                app,
                                "Enable:${isEnableCurrentPackageHook()} ${label}\t\n${loadPackageParam.packageName} ",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        if (isEnableCurrentPackageHook()) {
                            hookCurrentPackage(loadPackageParam)
                        }
                    }
                })
        }.onFailure {
            log("hook onCreate onFailure:${it.message}", this)
        }

    }

    fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage enable ${key}", this)
        if (isCurrentPackage(loadPackageParam = lpparam)) {
            execHook(lpparam)
        }
    }

}