package com.ml.xposedproject.hook.impl

import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.callbacks.XC_LoadPackage


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookZSCF : HookPackage {
    override val label: String = "芝士财富"
    override fun getPackage(): String {
        return "com.cheese.stock"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            hookUserInfo(loadPackageParam, "com.cheese.business_layer.event.PayEvent")
            hookUserInfo1(loadPackageParam, "com.cheese.business_layer.db.entity.UserInfo")
        }.onFailure {
            log("hookSelf onFailure:${it.message}", this)
        }

    }

    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam, className: String) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam, className, methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getEvent" to 0)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
           /* val payClazz = loadPackageParam.findClass("com.cheese.business_layer.event.PayEvent")
            val pay = payClazz.getConstructor(Int::class.java).newInstance(0)
            hookMethodParams(loadPackageParam,"com.cheese.model_mine.activity.MemberActivity","E0",
                listOf(pay),pay::class.java)*/
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }
    private fun hookUserInfo1(loadPackageParam: XC_LoadPackage.LoadPackageParam, className: String) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam, className, methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                //add("getEvent" to 0)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            hookMethodAndPrint(loadPackageParam,className,"getVipTime")
            hookMethodAndPrint(loadPackageParam,className,"getNewType")
            hookMethodAndPrint(loadPackageParam,className,"getStatus")
            hookMethodAndPrint(loadPackageParam,className,"getType")
            hookMethodAndPrint(loadPackageParam,className,"getDesc")
            hookAndReplaceMethod(loadPackageParam, "com.cheese.main.model.VipTypeModel", "getVipType", 2)
            hookAndReplaceMethod(loadPackageParam, "com.cheese.main.model.VipTypeModel", "getVipTime","${System.currentTimeMillis()+3600*24}")
            hookAndReplaceMethod(loadPackageParam, "com.cheese.model_tribe.model.VipTypeModel", "isVip",true)

        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

}