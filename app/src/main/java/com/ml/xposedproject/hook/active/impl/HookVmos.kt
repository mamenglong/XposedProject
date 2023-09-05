package com.ml.xposedproject.hook.active.impl

import android.content.Context
import androidx.core.app.NotificationCompat
import com.google.auto.service.AutoService
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.hook.ext.findAndHookMethod
import com.ml.xposedproject.hook.ext.findClass
import com.ml.xposedproject.hook.ext.hookPrintClassMethod
import com.ml.xposedproject.log
import com.ml.xposedproject.registerMethodHookCallback
import com.ml.xposedproject.registerMethodReplaceHookCallback
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.callbacks.XC_LoadPackage
import org.json.JSONObject
import java.lang.reflect.Method

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:22
 * Description: This is HookDy
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
@AutoService(HookPackage::class)
class HookVmos : HookPackage {
    private var plugin: String = ""
    private var cryptographicClasses: Class<*>? = null
    private var decryptDataMethod: Method? = null
    private var encryptDataMethod: Method? = null
    override val label: String = "VMos"
    override fun getPackage(): String {
        return "com.vmos.pro"
    }

    override fun hookCurrentPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("hookCurrentPackage:${loadPackageParam.packageName}", this)
        if (findClass(loadPackageParam, "com.vpi.core.utils.VpiNativeUtils")) {
            loadPackageParam.findAndHookMethod(
                "org.json.JSONObject", "getString",
                registerMethodHookCallback {
                    afterHookedMethod { param ->
                        runCatching {
                            log("org.json.JSONObject.getString param:$param")
                            if (param?.args?.getOrNull(0)
                                    .toString() == NotificationCompat.CATEGORY_MESSAGE) {
                                val str = String(
                                    decryptDataMethod?.invoke(
                                        null,
                                        context,
                                        param?.result
                                    ) as ByteArray
                                )
                                log("org.json.JSONObject.getString Dec:$str",this@HookVmos)
                                val jSONObject = JSONObject(str)
                                if (!jSONObject.isNull("data")) {
                                    var jSONObject2 = jSONObject.getJSONObject("data")
                                    if (jSONObject2.has("accessToken")) {
                                        jSONObject2.put("isForeverMember", 1)
                                        jSONObject2.put("isMember", 1);
                                        jSONObject2.put(
                                            "memberExpireTime",
                                            "4131-5-20 13:14:20"
                                        );
                                        jSONObject2.put("nickName", "Hooker")
                                        jSONObject2.put("userImg", "")
                                    } else if (jSONObject2.has("ads")) {
                                        jSONObject2 = null;
                                    } else if (jSONObject2.has("isShowAllExclusiveService")) {
                                        jSONObject2.put("isShowAllExclusiveService", 1);
                                    } else if (jSONObject2.has("activityEndTime")) {
                                        val jSONArray =
                                            jSONObject2.getJSONArray("goodResultList")
                                        (0..<jSONArray.length()).forEach {
                                            jSONArray.getJSONObject(it)
                                                .put("goodPrice", 520131400);
                                        }
                                    } else if (jSONObject2.has("systemPluginResult")) {
                                        log("systemPluginResult:$plugin")
                                        if (!"".equals(plugin)) {
                                            jSONObject2.put(
                                                "systemPluginResult",
                                                JSONObject(plugin)
                                            );
                                            plugin = ""
                                        }
                                    }
                                    jSONObject.put("data", jSONObject2);
                                    param?.setResult(
                                        encryptDataMethod?.invoke(
                                            null,
                                            context,
                                            jSONObject.toString()
                                        ).toString()
                                    )
                                }
                            }
                        }.onFailure {
                            log(
                                "hookCurrentPackage:${loadPackageParam.packageName},org.json.JSONObject.getString onFailure:${it.toString()}",
                                this
                            )
                        }
                    }
                }, String::class.java
            )
        }
    }

    private fun findClass(
        loadPackageParam: XC_LoadPackage.LoadPackageParam,
        str: String
    ): Boolean {
        var result = false
        if (cryptographicClasses != null) return result
        val loadEx = loadPackageParam.findClass(str, loadPackageParam.classLoader)
        log("findClass:${loadPackageParam.packageName} cls:$str loadEx:${loadEx}", this)
        runCatching {
            loadEx?.declaredMethods?.forEach {
                log(
                    "findClass:${loadPackageParam.packageName} cls:$str loadEx:${loadEx},method:$it",
                    this
                )
            }
            decryptDataMethod =
                loadEx!!.getDeclaredMethod("decryptData", Context::class.java, String::class.java)
            encryptDataMethod =
                loadEx.getDeclaredMethod("encryptData", Context::class.java, String::class.java)
            cryptographicClasses = loadEx

            log(
                "findClass:${loadPackageParam.packageName} cls:$str encryptDataMethod:${encryptDataMethod},decryptDataMethod:$decryptDataMethod",
                this
            )

            loadPackageParam.hookPrintClassMethod("com.vmos.pro.activities.main.fragments.PluginHelper")
            XposedBridge.hookAllMethods(
                loadPackageParam.findClass("com.vmos.pro.activities.main.fragments.PluginHelper"),
                "getPluginDownloadBean",
                registerMethodHookCallback {
                    beforeHookedMethod {
                        val booleanValue = it?.args?.getOrNull(0) == true
                        val obj = it?.args?.getOrNull(1).toString()
                        val str2 = it?.args?.getOrNull(2).toString()
                        log(
                            "findClass:${loadPackageParam.packageName} cls:$str getPluginDownloadBean:${booleanValue},${obj},${str2}",
                            this
                        )
                        if (!"GOOGLE_SERVICE".equals(obj)) {
                            when (str2) {
                                "9.0" -> {
                                    if (booleanValue && "ROOT".equals(obj)) {
                                        plugin =
                                            "{\"pluginMd5\":\"3bea8441b2aa46094f83d190e3ce778e\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/2022042610502584638.zip\",\"version\":\"0\"}";
                                    }
                                }

                                "7.1" -> {
                                    if (booleanValue) {
                                        if ("ROOT".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"3bea8441b2aa46094f83d190e3ce778e\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/android71root_plugin-64bit2.zip\",\"version\":\"0\"}";
                                        }
                                        if ("XPOSED".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"7c17dd64dd483f913abefcba1f04e1b3\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/android71xposed_plugin-64bit.zip\",\"version\":\"0\"}";
                                        }
                                    } else {
                                        if ("ROOT".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"4e95860cdd8aaa72f4ace25aaae89dbd\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/android71root_plugin-32bit2.zip\",\"version\":\"0\"}";
                                        }
                                        if ("XPOSED".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"5d965a2fa345c027afabf16df98ef0f4\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/android71xposed_plugin-32bit4.zip\",\"version\":\"0\"}";
                                        }
                                    }
                                }

                                "5.1" -> {
                                    if (booleanValue) {
                                        if ("ROOT".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"b4e21cf09d8495b06819592e33afce30\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/2022071919333713959.zip\",\"version\":\"1\"}";
                                        }
                                        if ("XPOSED".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"a9710d23ce275ee05a5c2b139c9e344b\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/2022051320085965680.zip\",\"version\":\"0\"}";
                                        }
                                    } else {
                                        if ("ROOT".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"b70c51a3868ce9d48cfbef77d14f8ad0\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/android51root_plugin-32bit2.zip\",\"version\":\"0\"}";
                                        }
                                        if ("XPOSED".equals(obj)) {
                                            plugin =
                                                "{\"pluginMd5\":\"1393b2afc74ebfcd350e18ee71c38999\",\"pluginUrl\":\"http://files.vmos.cn/vmospro/plugin/2022072822095923110.zip\",\"version\":\"0\"}";
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            )
            result = true
        }.onFailure {
            log("findClass:${loadPackageParam.packageName} onFailure:${it.toString()}", this)
            result = false
        }
        log(
            "findClass:${loadPackageParam.packageName} cls:$str result:${result},plugin:$plugin",
            this
        )
        return result
    }
}