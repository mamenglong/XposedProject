package com.ml.xposedproject.hook.impl

import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodReplaceHookCallback
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
class HookMMSH : HookPackage {
    override val label: String = "麦苗守护"
    override fun getPackage(): String {
        return "cn.imyfone.famiguard.parent"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage", this)
        hookUserInfo(loadPackageParam)
    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getUserType" to 1)
               // add("getVip" to 1)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            hookAndReplaceMethod(loadPackageParam,"com.imyfone.common.utils.DeviceManager","hasVipExpired", false)
            hookAndReplaceMethod(loadPackageParam,"com.imyfone.common.data.constant.UserManager","isExpired", false)
            hookMethodParams(loadPackageParam,"com.imyfone.parent.fragment.h.b","b",
                listOf(null,false),Int::class.java,Boolean::class.java)
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getExpiredTime")
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getNickName")
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getProductName")
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getProfile")
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getUserPass")
            hookMethodAndPrint(loadPackageParam,"com.imyfone.common.data.constant.UserManager.UserInfo",
                "getUserType")
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

}