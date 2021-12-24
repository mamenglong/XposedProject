package com.ml.xposedproject.hook.impl

import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookHXMH : HookPackage {
    override val label: String = "漫画"
    override fun getPackage(): String {
        return "com.yayd.app.cn.edu"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage", this)
        hookUserInfo(loadPackageParam)
      /*  hookVideoInfo(loadPackageParam)
        hookPlayer(loadPackageParam)
        hookLivePlayer(loadPackageParam)
        hookRemoveNotice(loadPackageParam)
        hookBannerInfo(loadPackageParam)*/
    }
    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getVipEndtime" to System.currentTimeMillis()+5*24*60*60*1000)
                add("isVip" to 1)
                add("getVip" to 1)
                add("isRecharge" to 0)
                add("getLevel" to 3)
                add("getPhone" to "15803942356")
                add("getUsername" to "tertwrte")
                add("getDiscountEndtime" to System.currentTimeMillis()+5*24*60*60*1000)
                add("getFreetime" to 11)
                add("getBookBean" to 1000)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            hookMethodAndPrint(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",
                "getId")
            hookMethodAndPrint(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",
                "getDiamondBonusDayN")
            hookMethodAndPrint(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",
                "getLevelName")
            hookMethodAndPrint(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",
                "getPassword")
            hookMethodAndPrint(loadPackageParam,"com.heyshow.comic.watch.data.bean.UserInfoBean",
                "getRegisterTime")
            hookAndReplaceMethodByCondition<Int>(loadPackageParam,"com.heyshow.comic.watch.data.bean.BaseResponse",
                "getCode",100,{result,params->
                    result==1009
                })
            
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

}