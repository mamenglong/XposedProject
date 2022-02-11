package com.ml.xposedproject.hook

import com.ml.xposedproject.DataItem
import com.ml.xposedproject.hook.base.HookPackage
import com.ml.xposedproject.hook.impl.*
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 2020/11/25 18:24
 * Description: This is HookFactory
 * Package: com.ml.xposedproject.hook
 * Project: XposedProject
 */
object HookFactory {
    private val hookList = mutableListOf<HookPackage>()
    private val mapHook = mutableMapOf<String,HookPackage>()

    init {
        hookList.add(HookSelf())
        hookList.add(HookOnePlusMultiApp())
        hookList.add(HookXYJMH())
        hookList.add(HookDy())
        hookList.add(HookHX())
        hookList.add(HookFTQ())
        hookList.add(ExportHook())
        hookList.add(HookHXMH())
        hookList.add(HookXCYS())
        hookList.add(HookHLW())
        hookList.add(HookZSCF())
        hookList.add(HookWYY())
        hookList.add(HookMMSH())
        hookList.add(HookICBC())
        hookList.add(HookGreen())
        hookList.forEach {
            mapHook[it.getPackage()] = it
        }
    }
    fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage listSize :${hookList.size}",this)
        mapHook.get(loadPackageParam.packageName)?.handleLoadPackage(loadPackageParam)
    }
    fun register(hook: HookPackage){
        hookList.add(hook)
    }
    fun mapDataItem():List<DataItem>{
        val list = mutableListOf<DataItem>()
        hookList.forEach{
            if (it !is HookSelf){
                list.add(DataItem(it.label,it.javaClass.simpleName))
            }
        }
        return list
    }

}