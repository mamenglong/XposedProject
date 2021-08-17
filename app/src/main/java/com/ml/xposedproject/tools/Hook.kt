package com.ml.xposedproject

import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlin.jvm.Throws

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/9/18 17:29
 * Description: This is Hook
 * Package: com.ml.xposedproject
 * Project: XposedProject
 */
fun registerMethodReplaceHookCallback(block: MethodReplaceHookCallback.() -> Unit): XC_MethodHook {
    return MethodReplaceHookCallback().apply(block)
}

fun registerMethodHookCallback(block: MethodHookCallback.() -> Unit): XC_MethodHook {
    return MethodHookCallback().apply(block)
}
fun interface ReplaceHookedMethod{
    /**
     * 替换函数
     * @return null 不替换 else 替换
     */
    fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam?): Any?
}
fun interface BeforeHookedMethod{
    fun beforeHookedMethod(param: XC_MethodHook.MethodHookParam?)
}
fun interface AfterHookedMethod{
    fun afterHookedMethod(param: XC_MethodHook.MethodHookParam?)
}
class MethodHookCallback: XC_MethodHook(){
    private var afterHookedMethod = AfterHookedMethod{

    }
    private var beforeHookedMethod = BeforeHookedMethod {

    }
    fun afterHookedMethod(block:AfterHookedMethod){
        afterHookedMethod = block
    }
    fun beforeHookedMethod(block:BeforeHookedMethod){
        beforeHookedMethod = block
    }
    override fun afterHookedMethod(param: MethodHookParam?) {
        super.afterHookedMethod(param)
        afterHookedMethod.afterHookedMethod(param)
    }

    override fun beforeHookedMethod(param: MethodHookParam?) {
        super.beforeHookedMethod(param)
        beforeHookedMethod.beforeHookedMethod(param)
    }
}
class MethodReplaceHookCallback: XC_MethodReplacement() {
    private var callback =  ReplaceHookedMethod{}

    fun replaceHookedMethod(callback: ReplaceHookedMethod){
        this.callback=callback
    }
    override fun replaceHookedMethod(param: XC_MethodHook.MethodHookParam?): Any? {
        return callback.replaceHookedMethod(param)
    }

}

@Throws(ClassNotFoundException::class)
fun XC_LoadPackage.LoadPackageParam.findClass(className:String): Class<*> {
    return XposedHelpers.findClass(className,classLoader)
}