package com.ml.xposedproject.hook.impl

import android.content.Context
import android.os.Bundle
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

import de.robv.android.xposed.XC_MethodHook


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookWYY : HookPackage {
    override val label: String = "网易云音乐"
    override fun getPackage(): String {
        return "com.netease.cloudmusic"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        hookUserInfo(loadPackageParam)
    }

    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(
                loadPackageParam,
                "com.netease.cloudmusic.meta.virtual.UserPrivilege",
                methodName,
                newValue
            )
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("isBlackVip" to true)
                add("isWhateverVip" to true)
                add("isAnnualVip" to true)
                add("isLuxuryMusicPackage" to true)
                add("isWhateverMusicPackage" to true)
                add("hasBlackVipRightsForProfileList" to true)
                add("hasMusicPackageRightsForProfileList" to true)
                add("getRedVipLevel" to 11)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            findAndHookMethod(loadPackageParam,
                "com.netease.cloudmusic.activity.LoadingActivity",
                "onCreate",
                registerMethodHookCallback {
                    afterHookedMethod {
                        showToast("跳过广告")
                        val maina = XposedHelpers.findClass("com.netease.cloudmusic.activity.MainActivity",loadPackageParam.classLoader)
                        XposedHelpers.callStaticMethod(maina,"G9",it?.thisObject,null,null)
                        XposedHelpers.callMethod(it?.thisObject, "finish")
                    }
                },
                Bundle::class.java
            )

        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
}