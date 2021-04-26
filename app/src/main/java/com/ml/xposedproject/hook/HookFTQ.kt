package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
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
class HookFTQ : HookPackage {
    override fun getPackage(): String {
        return "com.fotiaoqiang.android.vpn"
    }

    override fun enableHook(): Boolean {
        log("enableHook context:${AndroidAppHelper.currentPackageName()}  context:${AndroidAppHelper.currentApplication().packageName}",this)
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_FTQ) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }
    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage :${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.vpn.code.g.f",
                loadPackageParam.classLoader, "a", Context::class.java,String::class.java, registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("hookFTQ 启动检测  replaceHookedMethod argument2:${it?.args?.get(1)}", this@HookFTQ)
                        return@replaceHookedMethod false
                    }
                })

        }.onFailure {
            log("hookFTQ 启动检测 onFailure handleLoadPackage:${it}", this)
        }
        hookUserInfo(loadPackageParam)
        hookAndbackend(loadPackageParam)
        hookDialog(loadPackageParam)
    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.vpn.code.bean.UserExpirationReply",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getEexpire_at" to (System.currentTimeMillis()+10000)/1000)
                add("isIs_enabled" to true)
                add("getUserExpiration" to 86400 * 2)
                add("getEexpiration" to 1000)
                add("isIs_vip" to true)
                add("isVip_user_view" to true)
                add("getEexpire_type" to 2)
                add("getUserFlows" to 1024)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookAndbackend(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"andbackend",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
               // add("getUserBindStatus" to System.currentTimeMillis()+10000)
                add("getUserBindStatus" to 1)
                add("getUserExpiration" to 1000)
                add("getUserPro" to true)
                add("isPro" to 1)
                add("isUserMarkDanger" to false)
                add("isLocalTimeCorrect" to true)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookDialog(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        /**
         * 绑定弹窗 BindingDialog
         */
        hookAndReplaceMethod(loadPackageParam,"com.vpn.code.activity.MainActivity","s2",Unit)
        /**
         * 公告弹窗 BulletinsDialog
         */
        hookAndReplaceMethod(loadPackageParam,"com.vpn.code.activity.MainActivity","t2",Unit)



        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.vpn.code.activity.MainActivity",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            /**
             * hook LogUtil
             */
            hookMethodPrint(loadPackageParam,"com.vpn.code.g.h","a",String::class.java)
            hookMethodPrint(loadPackageParam,"com.vpn.code.g.h","c",String::class.java)
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

}