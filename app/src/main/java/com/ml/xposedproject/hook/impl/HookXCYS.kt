package com.ml.xposedproject.hook.impl

import android.widget.Toast
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
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
class HookXCYS : HookPackage {
    override val label: String = "追风视频"
    override fun getPackage(): String {
        return "com.huli.hlfilms"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            hookUserInfo(loadPackageParam)
            hookSplash(loadPackageParam)
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.xiaocao.p2p.entity.MineUserInfo",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getVip_validity" to (System.currentTimeMillis()+5*24*60*60*1000) /1000)
                add("getIs_vip" to 1)
                add("getNickname" to "哈哈哈哈哈")
                add("getPhone" to "15803942356")
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