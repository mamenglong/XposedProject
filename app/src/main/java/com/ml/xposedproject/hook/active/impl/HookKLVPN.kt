package com.ml.xposedproject.hook.active.impl

import android.app.Activity
import android.content.DialogInterface
import android.os.Bundle
import com.google.auto.service.AutoService
import com.google.gson.JsonObject
import com.ml.xposedproject.clearData
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.hook.active.base.PackageWithConfig
import com.ml.xposedproject.hook.ext.findAndHookMethod
import com.ml.xposedproject.hook.ext.hookAndReplaceMethod
import com.ml.xposedproject.log
import com.ml.xposedproject.randomString
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import com.ml.xposedproject.showToast
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import org.luckypray.dexkit.DexKitBridge
import org.luckypray.dexkit.query.enums.StringMatchType
import java.util.Calendar

@AutoService(HookPackage::class)
class HookKLVPN : PackageWithConfig() {
    override val label: String = "快连 VPN"
    override fun getPackage(): String {
        return "world.letsgo.booster.android.pro"
    }

    private var id: String? = null

    override fun exec(loadPackageParam: LoadPackageParam, jsonObject: JsonObject) {
        val resetTime = jsonObject.get("ResetTime").asLong
        if (System.currentTimeMillis() > resetTime) {
            showToast("已过期,重置配置.")
            context?.clearData()
            System.exit(1)
            return
        }
        loadPackageParam.hookAndReplaceMethod(
            "world.letsgo.booster.android.data.bean.Account",
            "getName",
            "Viper"
        )
    }

    override fun init(loadPackageParam: LoadPackageParam) {
        val calendar = Calendar.getInstance()
        val timeInMillis = calendar.getTimeInMillis()
        calendar.add(Calendar.DAY_OF_MONTH, 3)
        val timeInMillis2 = calendar.getTimeInMillis()
        saveConfig {
            it.put("LoadingTime", timeInMillis)
            it.put("ResetTime", timeInMillis + 30)
            it.put("ResetTime", timeInMillis2)
        }.also {
            hook(loadPackageParam)
        }
    }

    override fun isEnableCurrentPackageHook(): Boolean {
        log("isEnableCurrentPackageHook key:$key enable:true", this)
        return true
    }

    private fun hook(loadPackageParam: LoadPackageParam) {
        loadPackageParam.hookAndReplaceMethod(
            "world.letsgo.booster.android.data.bean.Account",
            "getLevelTitle",
            "铂金会员"
        )
        loadPackageParam.hookAndReplaceMethod(
            "world.letsgo.booster.android.data.bean.Account",
            "getUserLevel",
            "platinum"
        )
        loadPackageParam.findAndHookMethod(
            "world.letsgo.booster.android.data.bean.Account",
            "getGearId",
            registerMethodHookCallback {
                afterHookedMethod {
                    id = it?.result as? String
                    log("getGearId:${id}", this@HookKLVPN)
                }
            }
        )

        DexKitBridge.create(loadPackageParam.appInfo.sourceDir)?.use { bridge ->
            bridge.findClass {
                searchPackages("androidx.appcompat.app")
                matcher {
                    interfaces {
                        add {
                            className(DialogInterface::class.java.name)
                        }
                    }
                    methods {
                        add {
                            usingStrings(
                                arrayListOf("show", "dismiss"),
                                StringMatchType.Equals
                            )
                        }
                    }
                }
            }?.also {
                log("findshowMethod:${it.size}", this)
            }?.forEach {
                log("findshowMethod:${it.name}", this)
                loadPackageParam.findAndHookMethod(
                    it.name, "show",
                    registerMethodHookCallback {
                        afterHookedMethod {
                            log("show:${it?.thisObject}", this@HookKLVPN)
                            (it?.thisObject as? DialogInterface)?.dismiss()
                        }
                    }
                )
            }
            //取消弹窗
            bridge.findMethod {
                matcher {
                    usingStrings(
                        arrayListOf("context", "dialogDismissListener"),
                        StringMatchType.Equals
                    )
                }
            }.getOrNull(0)?.also {
                log("findMethod:${it.className}->${it.methodName}", this)
                loadPackageParam.hookAndReplaceMethod(
                    it.getMethodInstance(loadPackageParam.classLoader),
                    null
                )
            }
            var clz: Class<*>? = null
            var methedName: String? = null
            bridge.findClass {
                matcher {
                    usingStrings(arrayListOf("it.imgBack"), StringMatchType.Equals)
                }
            }.getOrNull(0)?.also {
                clz = it.getInstance(loadPackageParam.classLoader)
                bridge.findMethod {
                    searchInClass(arrayListOf(it))
                    matcher {
                        usingStrings(
                            arrayListOf("private fun execReferrer…To(mAutoDisposable"),
                            StringMatchType.Contains
                        )
                    }
                }.getOrNull(0)?.also {
                    methedName = it.name
                }
            }
            bridge.findMethod {
                matcher {
                    usingStrings(arrayListOf("Settings.S…D"), StringMatchType.Contains)
                }
            }.getOrNull(0)?.also {
                log("findMethod:${it.className}->${it.methodName}", this)
                val android = randomString(16)
                loadPackageParam.findAndHookMethod(it.className, it.methodName,
                    registerMethodReplaceHookCallback {
                        replaceHookedMethod { p ->
                            android.also {
                                log("findIdAndHook old:${p?.result},new:${it}", this@HookKLVPN)
                            }
                        }
                    })
            }
            bridge.findMethod {
                matcher {
                    declaredClass("world.letsgo.booster.android.pages.home.HomeActivity")
                    addParamType(Bundle::class.java.name)
                    paramCount = 1
                }
            }.getOrNull(0)?.also {
                log("findMethod:${it.className}->${it.methodName}", this)
                loadPackageParam.findAndHookMethod(
                    it.className, it.methodName,
                    registerMethodHookCallback {
                        afterHookedMethod { param ->
                            runCatching {
                                log(
                                    "HomeActivity clz:${clz},methedName:${methedName}",
                                    this@HookKLVPN
                                )
                                XposedBridge.unhookMethod(param?.method, this)
                                XposedHelpers.callMethod(
                                    clz?.newInstance(),
                                    methedName,
                                    id ?: "121004726"
                                )
                            }.onFailure {
                                it.stackTraceToString()
                                    .also { log("HomeActivity error:${it}", this@HookKLVPN) }
                            }
                        }
                    }, *it.paramTypeNames.toTypedArray()
                )
            }
        }
        showToast("适配完成.")
    }
}