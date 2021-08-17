package com.ml.xposedproject.hook.impl

import android.app.Activity
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.showToast
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
    override val label: String = "抖音"
    override fun getPackage(): String {
        return "com.user.ccnineonepros.android"
    }

    override fun hookPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookPackage:${loadPackageParam.packageName}", this)
        hookUserInfo(loadPackageParam)
        //hookLog(loadPackageParam)

        kotlin.runCatching {

        }.onFailure {
            log("onFailure getInfo :${it}", this)
        }
    }

    private fun hookLog(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        this.hookMethodAndPrint(loadPackageParam,"com.blankj.utilcode.util.LogUtils","b",Any::class.java)
    }

    private fun hookVideoDetail(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookVideoMethod(methodName: String,newValue: Any?){
            hookAndReplaceMethod(loadPackageParam,"com.niming.weipa.model.VideoInfo2",methodName, newValue)
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
            hookAndReplaceMethod(loadPackageParam,"com.niming.weipa.model.VideoDetails.AuthError","getKey", 1002)
            hookAndReplaceMethod(loadPackageParam,"com.niming.framework.net.Result","isOk", true)
            hookAndReplaceMethod(loadPackageParam,"com.shuyu.gsyvideoplayer.video.base.GSYVideoView","getDuration", 5*60*1000)
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
        kotlin.runCatching {
            setObjectField(loadPackageParam,"com.ninetyone.pron.android.bean.UserInfoBean","nickname","hahahahh")
            log("onSuccess hookUserInfo", this)
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