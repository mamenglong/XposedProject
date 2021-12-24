package com.ml.xposedproject.hook.impl

import android.widget.Toast
import com.ml.xposedproject.*
import com.ml.xposedproject.hook.base.HookPackage
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.lang.reflect.Array


/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:18
 * Description: This is HookXYJMH
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
class HookHLW : HookPackage {
    override val label: String = "葫芦娃"
    override fun getPackage(): String {
        return "com.adult.zero"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage ${loadPackageParam.packageName}", this)
        kotlin.runCatching {
            hookUserInfo(loadPackageParam, "com.hlw.movie.commonservice.cache.entity.SessionUser")
            hookUserInfo1(loadPackageParam, "com.hlw.movie.commonservice.cache.MMKVSessionManager")
            hookUserInfo2(loadPackageParam, "com.hlw.movie.commonservice.ad.bean.PVideoDetail")
            val newValue = """
                        {"agentPrice":198.0,"appAgent":null,"appAgentRate":null,"attentionCount":0,"attestType":0,"attestVid":0,"avatarId":10,"avatarPath":"/WlRti1sAEa/img/20200529/b29e06bc27405948ffc600604b286918.png","bigCustomerTime":"[10,21]","blockChainWalletAndroid":null,"blockChainWhiteUrl":null,"canIdentifyCard":1,"carryOutPromoteNum":0,"castScreen":true,"cellphone":"18438603985","countryCode":"+86","createPotatoUrl":"https://ptcc.in/hlwcz","currLevelInvitationCount":0,"currentMoney":0.0,"currentPersent":0.0,"doubleSpeedPlay":true,"downloadElse":-1,"downloadEveryday":0,"email":null,"fanCount":0,"forbiddenState":0,"functionDTOListStr":"","gender":"0","giveVip":true,"giveVipDays":3,"globalCanIdentifyCard":1,"hasBigCustomer":false,"hasFirstPay":false,"identifyCard":"HiRQrdh0vfP0WN4Q2J+hS9gnOlvR4+k2XZXKJh9B+7QIYnKmF+17iLKN4sEG1k2F","invitation":false,"invitationCode":"01kd89","invitationCount":0,"invitationGiveDays":0,"invitationLevel":0,"invitationNextLevelLeft":1,"isDownloadNolimit":true,"isFullHdHigh":true,"isHdHigh":true,"isHigh":true,"isViewNoLimit":true,"isWithdrawPwd":false,"likeCount":0,"maxLevelRate":0.0,"monthVipPrice":60,"nickName":"用户2629881","openWalletGoods":true,"promoteNum1":2,"promoteNum2":12,"promoteNum3":6,"promoteVipSwitch":true,"promotionAgent":false,"promotionNumber":0,"promotionPlatformUrl":"https://api.756025.com","promotionSwitch":false,"rakeBackRate":0.0,"redeemDiscount":38.0,"reviewRule":null,"shortLastPlaySize":-1,"showTitle":0,"showTitleName":"新手","signUpRewardNum":1,"signUpRewardType":1,"signature":null,"state":0,"topLevel":false,"totalPromotion":0.0,"totalRecharge":0,"upLevel":null,"userId":28656659,"userType":1,"viewElse":0,"viewEveryday":1,"vipAuth":["1","2","3","4","5","6","8"],"vipExpireTime":"2021/08/15","vipExpireTimestamp":null,"vipType":0,"welfareBalNum":0,"welfareTotalNum":0,"withdrawTimeLimit":"{\"unionpay\":[10,20],\"alipay\":[10,20],\"otc\":[10,20]}","yearVipPrice":280,"yellowState":1}  
          """.trimIndent()

            /*      hookAndReplaceMethodByCondition<String>(
                      loadPackageParam,
                      "com.tencent.mmkv.MMKV", "decodeString", newValue,
                      { oldValue, params ->
                          params?.find {
                              it.equals("session_key_user")
                          } != null
                          false
                      }, String::class.java
                  )*/
            hookSplash(loadPackageParam)
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
                add("getVipExpireTime" to (System.currentTimeMillis() + 5 * 24 * 60 * 60 * 1000).toString())
                //add("getVipExpireTimestamp" to (System.currentTimeMillis()+5*24*60*60*1000).toString())
                add("isSvip" to true)
                add("isVip" to true)
                add("isForeverSVIP" to true)
                add("getVipType" to 8)
                add("getViewEveryday" to 100)
                add("getViewElse" to 100)
                add("isViewNoLimit" to true)
                add("getCurrentMoney" to 100.0)
                add("getStatus" to 6)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            this.hookMethodAndPrint(
                loadPackageParam,
                className,
                "setVipExpireTime",
                String::class.java
            )
            this.hookMethodAndPrint(
                loadPackageParam,
                className,
                "setVipExpireTimestamp",
                String::class.java
            )
            this.hookMethodAndPrint(
                loadPackageParam, className, "setVipAuth",
                Array.newInstance(String::class.java, 2).javaClass
            )
            this.hookMethodAndPrint(
                loadPackageParam,
                "com.hlw.movie.commonservice.cache.MMKVSessionManager",
                "getSessionUser",
                Array.newInstance(String::class.java, 2).javaClass
            )
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun hookUserInfo1(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String
    ) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam, className, methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("isSVipUser" to true)
                add("isVip" to true)
                add("isVipPermissions" to true)
                add("getVipType" to 8)
                add("isYearCardVipUser" to 100)
                add("isMoneyVipPermissions" to true)
                add("isNomalUser" to false)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
            hookMethodParams(
                loadPackageParam,
                className,
                "setLongVideoCount",
                100,
                0,
                Int::class.java
            )
            hookMethodAndPrint(loadPackageParam, className, "getOpenInstallData")
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun hookUserInfo2(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        className: String
    ) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethod(loadPackageParam, className, methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("isNeedDeductTimes" to false)
                add("isPaidVideo" to false)
                add("getPrice" to 0)
                add("isIsFree" to true)
                add("isCanPlay" to true)
                add("isNeedPay" to false)
                add("isLike" to true)

            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }

        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun hookSplash(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            findAndHookMethod(loadPackageParam,
                "com.hlw.movie.splash.mvp.ui.activity.SplashActivity",
                "startCountdown",
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        Toast.makeText(context, "跳过广告", Toast.LENGTH_SHORT).show()
                        XposedHelpers.setBooleanField(it!!.thisObject, "curDisplayADNow", true)
                        XposedHelpers.callMethod(it!!.thisObject, "onResume")
                    }
                })
            findAndHookMethod(loadPackageParam,
                "com.hlw.movie.common.base.HLWBaseActivity",
                "initNoticePop",
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        Toast.makeText(context, "跳过弹窗1", Toast.LENGTH_SHORT).show()
                    }
                })
            findAndHookMethod(
                loadPackageParam,
                "com.hlw.movie.common.base.HLWBaseActivity",
                "showNoticeView",
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        Toast.makeText(context, "跳过弹窗2", Toast.LENGTH_SHORT).show()
                    }
                },
                Boolean::class.java
            )
        }
    }
}