package com.ml.xposedproject.hook

import android.app.Activity
import android.app.AndroidAppHelper
import android.view.View
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.showToast
import com.ml.xposedproject.tools.Config
import de.robv.android.xposed.XC_MethodReplacement
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
class HookDy : HookPackage {
    override fun enableHook(): Boolean {
        log(
            "enableHook context:${AndroidAppHelper.currentPackageName()}  context:${context?.packageName}",
            this
        )
        val enable = context?.let { Config.getBool(it, Config.KEYS.ENABLE_DY) } ?: false
        log("enableHook enable:$enable", this)
        return enable
    }

    override fun getPackage(): String {
        return "com.wechat.yajiji1123"
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage", this)
        hookUserInfo(loadPackageParam)
        removeAd(loadPackageParam)
        hookVideoDetail(loadPackageParam)
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.niming.weipa.model.VideoDetails.AuthError",
                loadPackageParam.classLoader, "getInfo", registerMethodHookCallback {
                    afterHookedMethod {
                        log("getInfo:${it?.result}  ${it?.args?.size}", this)
                        //it?.result = "超级至尊"
                        log("getInfo:${it?.result}  ${it?.args}", this)
                    }
                })

        }.onFailure {
            log("onFailure getInfo :${it}", this)
        }
    }

    private fun hookVideoDetail(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookVideoMethod(methodName: String,newValue: Any?){
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.niming.weipa.model.VideoInfo2",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any?>>()
            list.apply {
                add("getCoins" to 4)
                add("getIs_free" to 1)
                add("getTitle" to "视频描述")
                add("getDuration" to 5*60*1000)
            }
            list.forEach {
                hookVideoMethod(it.first, it.second)
            }
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.niming.weipa.model.VideoDetails.AuthError","getKey", 1002)
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.niming.framework.net.Result","isOk", true)
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.shuyu.gsyvideoplayer.video.base.GSYVideoView","getDuration", 5*60*1000)
            XposedHelpers.findAndHookMethod("com.niming.weipa.model.VideoPlayTimeModel",
                loadPackageParam.classLoader, "setTime", String::class.java,registerMethodHookCallback {
                    afterHookedMethod {
                        log("setTime:${it?.args?.get(0)} ", this@HookDy)
                    }
                })
            XposedHelpers.findAndHookMethod("com.shuyu.gsyvideoplayer.video.base.GSYVideoControlView.b",
                loadPackageParam.classLoader, "run", String::class.java,XC_MethodReplacement.DO_NOTHING)

        }.onFailure {
            log("onFailure hookVideoDetail:${it}", this)
        }
    }

    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookUserInfoMethod(methodName: String, newValue: Any) {
            hookAndReplaceMethodPrintOrigin(loadPackageParam,"com.niming.weipa.model.UserInfo2",methodName, newValue)
        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getIs_vip" to "y")
                add("getNick" to "小可爱")
                add("getCoins" to 300)
                add("getRank_type" to 9)
                add("getVideo_long_count" to 10)
                add("getVideo_short_count" to 10)
                add("getVip_expired" to "2020-11-27 22:22:22")
                add("getVip_expired_text" to "2020-11-27 22:22:22")
                add("getVip_text" to "超级至尊")
                add("getBuy_floor_count" to 100)
                add("getBuy_image_count" to 100)
                add("getBuy_short_count" to 100)
                add("getBuy_long_count" to 100)
            }
            list.forEach {
                hookUserInfoMethod(it.first, it.second)
            }
        }.onFailure {
            log("onFailure hookUserInfo:${it}", this)
        }
    }

    private fun removeAd(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.niming.weipa.ui.lock.a",
                loadPackageParam.classLoader, "onActivityStarted",
                Activity::class.java,
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("onActivityStarted ${it?.args?.get(0)}", this@HookDy)
                        return@replaceHookedMethod null
                    }
                })
            XposedHelpers.findAndHookMethod("com.niming.weipa.ui.lock.a",
                loadPackageParam.classLoader, "onActivityStopped",
                Activity::class.java,
                registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log("onActivityStopped ${it?.args?.get(0)}", this@HookDy)
                        return@replaceHookedMethod null
                    }
                })
            XposedHelpers.findAndHookMethod("com.niming.weipa.App",
                loadPackageParam.classLoader, "onCreate", registerMethodHookCallback {
                    afterHookedMethod {
                        log("App onCreate ", this@HookDy)
                        showToast("App onCreate1")
                    }
                })

        }.onFailure {
            log("onFailure:${it}", this)
        }
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.niming.weipa.ui.splash.SplashAdActivity",
                loadPackageParam.classLoader, "onResume", registerMethodHookCallback {
                    afterHookedMethod {
                        log("SplashAdActivity onResume ${it?.thisObject}", this@HookDy)
                        XposedHelpers.callMethod(it?.thisObject, "b")
                    }
                })

        }.onFailure {
            log("onFailure SplashAdActivity onResume :${it}", this)
        }
    }

 }