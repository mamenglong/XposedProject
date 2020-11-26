package com.ml.xposedproject.hook

import android.app.Activity
import android.app.AndroidAppHelper
import android.widget.Toast
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.showToast
import com.ml.xposedproject.tools.Config
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

    private fun hookUserInfo(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        fun hookMethod(methodName: String, newValue: Any) {
            XposedHelpers.findAndHookMethod("com.niming.weipa.model.UserInfo2",
                loadPackageParam.classLoader, methodName, registerMethodReplaceHookCallback {
                    replaceHookedMethod {
                        log(methodName, this@HookDy)
                        return@replaceHookedMethod newValue
                    }
                })

        }
        kotlin.runCatching {
            val list = mutableListOf<Pair<kotlin.String, kotlin.Any>>()
            list.apply {
                add("getIs_vip" to "y")
                add("getNick" to "小可爱")
                add("getCoins" to 300)
                add("getRank_type" to 9)
                add("video_long_count" to 10)
                add("video_short_count" to 10)
                add("vip_expired" to "2020-11-27 22:22:22")
                add("vip_expired_text" to "2020-11-27 22:22:22")
                add("vip_text" to "超级至尊")
                add("buy_floor_count" to 100)
                add("buy_image_count" to 100)
                add("buy_long_count" to 100)
                add("buy_short_count" to 100)
            }
            list.forEach {
                hookMethod(it.first, it.second)
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
                        showToast("App onCreate")
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