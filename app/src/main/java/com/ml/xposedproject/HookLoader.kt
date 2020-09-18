package com.ml.xposedproject

import android.app.Application
import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import dalvik.system.PathClassLoader
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.IXposedHookZygoteInit
import de.robv.android.xposed.IXposedHookZygoteInit.StartupParam
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import java.io.File

class HookLoader : IXposedHookLoadPackage, IXposedHookZygoteInit {
    /**
     * 实际hook逻辑处理类
     */
    private val handleHookClass = XposedInit::class.java.name

    /**
     * 实际hook逻辑处理类的入口方法
     */
    private val handleHookMethod = "handleLoadPackage"
    private val initMethod = "initZygote"
    private var startupparam: StartupParam? = null

    /**
     * 重定向handleLoadPackage函数前会执行initZygote
     *
     * @param loadPackageParam
     * @throws Throwable
     */
    @Throws(Throwable::class)
    override fun handleLoadPackage(loadPackageParam: LoadPackageParam) {
        // 排除系统应用
        if (loadPackageParam.appInfo == null ||
            loadPackageParam.appInfo.flags and (ApplicationInfo.FLAG_SYSTEM or ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1
        ) {
            return
        }
        //将loadPackageParam的classloader替换为宿主程序Application的classloader,解决宿主程序存在多个.dex文件时,有时候ClassNotFound的问题
        XposedHelpers.findAndHookMethod(
            Application::class.java,
            "attach",
            Context::class.java,
            object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun afterHookedMethod(param: MethodHookParam) {
                    val context = param.args[0] as Context
                    loadPackageParam.classLoader = context.classLoader
                    val cls = getApkClass(context, modulePackageName, handleHookClass)
                    val instance = cls.newInstance()
                    try {
                        cls.getDeclaredMethod(initMethod, startupparam!!.javaClass)
                            .invoke(instance, startupparam)
                    } catch (e: NoSuchMethodException) {
                        // 找不到initZygote方法
                        log("afterHookedMethod",this@HookLoader)
                    }
                    cls.getDeclaredMethod(handleHookMethod, loadPackageParam.javaClass)
                        .invoke(instance, loadPackageParam)
                }
            })
    }

    /**
     * 实现initZygote，保存启动参数。
     *
     * @param startupParam
     */
    override fun initZygote(startupParam: StartupParam) {
        startupparam = startupParam
    }

    @Throws(Throwable::class)
    private fun getApkClass(
        context: Context,
        modulePackageName: String,
        handleHookClass: String
    ): Class<*> {
        log("getApkClass modulePackageName:$modulePackageName", this)
        val apkFile = findApkFile(context, modulePackageName)
            ?: throw RuntimeException("寻找模块apk失败")
        log("getApkClass apkFile:${apkFile.absolutePath}", this)
        //加载指定的hook逻辑处理类，并调用它的handleHook方法
        val pathClassLoader =
            PathClassLoader(apkFile.absolutePath, ClassLoader.getSystemClassLoader())
        return Class.forName(handleHookClass, true, pathClassLoader)
    }

    /**
     * 根据包名构建目标Context,并调用getPackageCodePath()来定位apk
     *
     * @param context           context参数
     * @param modulePackageName 当前模块包名
     * @return apk file
     */
    private fun findApkFile(context: Context?, modulePackageName: String): File? {
        log("findApkFile",this)
        if (context == null) {
            return null
        }
        try {
            val moudleContext = context.createPackageContext(
                modulePackageName,
                Context.CONTEXT_INCLUDE_CODE or Context.CONTEXT_IGNORE_SECURITY
            )
            val apkPath = moudleContext.packageCodePath
            log("findApkFile success $apkPath",this)
            return File(apkPath)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            log("findApkFile PackageManager.NameNotFoundException:$e",this)
        }
        return null
    }

    companion object {
        const val TAG = "HookLoader"
        //按照实际使用情况修改下面几项的值
        /**
         * 当前Xposed模块的包名,方便寻找apk文件
         */
        private const val modulePackageName = BuildConfig.APPLICATION_ID
    }
}