package com.ml.xposedproject.hook.impl

import android.widget.Toast
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookHX : HookPackage {
    override val label: String = "红杏"
    override fun getPackage(): String {
        return "com.bepskq.hxgohq"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage", this)
        removeSplashAd(loadPackageParam)
        hookUserInfo(loadPackageParam)
        hookVideoInfo(loadPackageParam)
        hookPlayer(loadPackageParam)
        hookLivePlayer(loadPackageParam)
        hookRemoveNotice(loadPackageParam)
        hookBannerInfo(loadPackageParam)
    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.bepskq.hxgohq.model.remote.User",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getVipTime" to System.currentTimeMillis()+10000)
                add("isVip" to true)
                add("isRecharged" to true)
                add("isDrag" to true)
                add("isStuck" to true)
                add("getGold" to 100)
                add("getUsername" to "tertwrte")
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookVideoInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.bepskq.hxgohq.model.remote.VideoInfo",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("isBuy" to true)
                add("isFavorite" to true)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookBannerInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,
                "com.bepskq.hxgohq.model.remote.Banner",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getCover" to "")
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun hookPlayer(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.bepskq.hxgohq.model.jsondata.DataVideoDetail",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getAdPages" to emptyList<Any>())
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookLivePlayer(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.bepskq.hxgohq.model.remote.LiveDetail",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("isBuy" to true)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookRemoveNotice(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.bepskq.hxgohq.model.jsondata.DataSystemInfo",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getNotice" to "")
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun removeSplashAd(loadPackageParam: XC_LoadPackage.LoadPackageParam){
        XposedHelpers.findAndHookMethod("com.bepskq.hxgohq.ui.activity.splash.SplashActivity",
            loadPackageParam.classLoader,
            "onResume",
            registerMethodHookCallback {
                afterHookedMethod{
                    Toast.makeText(context,"跳过广告", Toast.LENGTH_SHORT).show()
                    XposedHelpers.callMethod(it!!.thisObject,"toMainView")
                }
            })

    }

}