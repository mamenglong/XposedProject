package com.ml.xposedproject.hook.active

import com.ml.xposedproject.DataItem
import com.ml.xposedproject.hook.active.base.HookPackage
import com.ml.xposedproject.hook.active.impl.HookSelf
import com.ml.xposedproject.log
import de.robv.android.xposed.callbacks.XC_LoadPackage
import java.util.ServiceLoader

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
    private val mapHook = mutableMapOf<String, HookPackage>()

    init {
        ServiceLoader.load(HookPackage::class.java, HookFactory::class.java.classLoader).let {
            it.forEach {
                hookList.add(it)
            }
        }
        hookList.forEach {
            mapHook[it.getPackage()] = it
        }

    }
    fun handleLoadPackage(loadPackageParam: XC_LoadPackage.LoadPackageParam) {
        log("handleLoadPackage listSize :${hookList.size}",this)
        mapHook.get(loadPackageParam.packageName)?.handleLoadPackage(loadPackageParam)
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