package com.ml.xposedproject.hook

import android.app.AndroidAppHelper
import android.content.Context
import android.widget.Toast
import com.ml.xposedproject.*
import com.ml.xposedproject.test.TestFiled
import com.ml.xposedproject.test.TestObject
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import de.robv.android.xposed.XC_MethodHook.MethodHookParam

import de.robv.android.xposed.XC_MethodHook




/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookHLW : HookPackage {
    override fun enableHook(): Boolean {
        log(
            "enableHook context:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",
            this
        )
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_XCYS) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }

    override fun getPackage(): String {
        return "com.adult.zero"
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            hookUserInfo(loadPackageParam)
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethodAndPrintResult(loadPackageParam,"com.hlw.movie.commonservice.cache.entity.SessionUser",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getVipExpireTime" to (System.currentTimeMillis()+5*24*60*60*1000).toString())
                add("isSvip" to true)
                add("isForeverSVIP" to true)
                add("getVipType" to 8)
                add("getViewEveryday" to 100)
                add("getViewElse" to 100)
                add("isViewNoLimit" to true)
                add("getCurrentMoney" to 100.0)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }

        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun hookSplash(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        kotlin.runCatching {
            findAndHookMethod(loadPackageParam,"com.xiaocao.p2p.ui.login.splash.SplashActivity","initData",
                registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        Toast.makeText(context,"跳过广告", Toast.LENGTH_SHORT).show()
                        XposedHelpers.callMethod(it!!.thisObject,"goToMain")
                    }
                })
        }
    }
}