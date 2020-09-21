package com.ml.xposedproject

import android.content.Context
import de.robv.android.xposed.*
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.lang.reflect.Method
import java.lang.reflect.Modifier

class XposedInit : IXposedHookLoadPackage, IXposedHookZygoteInit {
    private var sharedPreferences: XSharedPreferences? = null
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        log("loadPackageParam.packageName:${loadPackageParam.packageName}")
        if (modulePackageName== loadPackageParam.packageName) {
            XposedHelpers.findAndHookMethod(
                MainActivity::class.java.name,
                loadPackageParam.classLoader,
                "getInfo", registerMethodReplaceHookCallback {
                    replaceHookedMethod{
                        log("replaceHookedMethod $it")
                        return@replaceHookedMethod "Hook succeed"
                    }
                })
        }else if("com.yyets.pro"==loadPackageParam.packageName){
            hookYYets(loadPackageParam)
        }else if ("com.xjlmh.classic"==loadPackageParam.packageName){
            hookXYJMH(loadPackageParam)
        }
    }

    private fun hookYYets(loadPackageParam: LoadPackageParam) {
        log(" com.yyets.pro")
        kotlin.runCatching {
            XposedHelpers.findAndHookMethod("com.baidu.protect.StubApplication",
                loadPackageParam.classLoader,
                "attachBaseContext",
                Context::class.java,
                object : XC_MethodHook() {
                    override fun afterHookedMethod(param: MethodHookParam?) {
                        super.afterHookedMethod(param)
                        //获取到Context对象，通过这个对象来获取classloader
                        val context = param?.args?.get(0) as Context?
                        //获取classloader，之后hook加固后的就使用这个classloader
                        val classLoader = context?.classLoader
                        log("classLoader:$classLoader")
                        classLoader?.let {classLoader->

                            val cls= XposedHelpers.findClass("com.yyets.zimuzu.util.ZimuzuHelper",classLoader)
                            XposedHelpers.setStaticBooleanField(cls,"isSupperUser",true)
                            XposedHelpers.setStaticIntField(cls,"is_internal_group",1)
                            return@let
                            val c =
                                classLoader.loadClass("com.yyets.zimuzu.net.model.UserInfoModel");
                            //关键一步，获取Class对象
                            var method: Method? = null
                            for (m in c.declaredMethods) {//获取loadAd的实例
                                if (m.name == "getGroup_type") {
                                    method = m
                                }
                            }
                            log("Module ${method.toString()}")//查看loadAd是否获取成功
                            XposedBridge.hookMethod(method, object : XC_MethodReplacement() {
                                override fun replaceHookedMethod(param: MethodHookParam?): Any {
                                    kotlin.runCatching {
                                        val clazz = Class.forName("com.yyets.zimuzu.util.ZimuzuHelper")
                                        XposedHelpers.setStaticBooleanField(clazz,"isSupperUser",true)
                                        XposedHelpers.setStaticIntField(clazz,"is_internal_group",1)
                                    }.onFailure {
                                        it.printStackTrace()
                                        log("replaceHookedMethod:${it.stackTraceToString()}")
                                    }
                                    return "1"
                                }

                            })

                        }
                    }
                })
        }.onFailure {
            log("handleLoadPackage:${it.message}")
        }
    }
    private fun hookXYJMH(loadPackageParam: LoadPackageParam){
        kotlin.runCatching {
           XposedHelpers.findAndHookMethod("com.maibaapp.module.main.bean.user.NewElfUserInfoDetailBean",
               loadPackageParam.classLoader,"isVip", registerMethodReplaceHookCallback {
                   replaceHookedMethod{
                        log("hookXYJMH replaceHookedMethod ")
                       return@replaceHookedMethod true
                   }
               })

        }.onFailure {
            log("handleLoadPackage:${it.message}")
        }
    }
    override fun initZygote(startupParam: StartupParam) {
        sharedPreferences = XSharedPreferences(modulePackageName, "default")
        log("$modulePackageName initZygote", this)
    }

    companion object {
        private const val modulePackageName = BuildConfig.APPLICATION_ID
    }
}