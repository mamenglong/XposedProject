package com.ml.xposedproject.hook.impl

import android.content.Context
import com.ml.xposedproject.hook.base.HookPackage
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
class HookFTQ : HookPackage {
    override val label: String = "佛跳墙"
    override fun getPackage(): String {
        return "com.fotiaoqiang.android.vpn"
    }
    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
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
        //hookUserInfo(loadPackageParam)
        hookAndbackend(loadPackageParam)
        hookDialog(loadPackageParam)
    }
    private fun hookAndbackend(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"andbackend.Andbackend",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
               // add("getUserBindStatus" to System.currentTimeMillis()+10000)
                //add("getUserBindStatus" to 1)
                //add("getUserExpiration" to 1000)
                add("getUserPro" to true)
                add("isPro" to 1L)
                add("isUserMarkDanger" to false)
                add("isLocalTimeCorrect" to true)
                val ex = 86400 *3
                add("expiration" to """
                    {"UserFlows":10737418240,"UserExpiration":$ex,"is_enabled":true,"expire_at":1679174094,"is_vip":true,"eexpire_at":1679174094,"eexpire_type":1,"eexpiration":1679174094,"eevip_balanceflow":100}
                """.trimIndent())
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
        this.hookAndReplaceMethod(loadPackageParam,"com.vpn.code.activity.MainActivity","s2",Unit)
        /**
         * 公告弹窗 BulletinsDialog
         */
        this.hookAndReplaceMethod(loadPackageParam,"com.vpn.code.activity.MainActivity","t2",Unit)



        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.vpn.code.activity.MainActivity",methodName, newValue)
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
            this.hookMethodAndPrint(loadPackageParam,"com.vpn.code.g.h","a",String::class.java.name)
            this.hookMethodAndPrint(loadPackageParam,"com.vpn.code.g.h","c",String::class.java.name)
            this.hookMethodAndPrint(loadPackageParam,"com.vpn.code.g.h","b",String::class.java,String::class.java)
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

}