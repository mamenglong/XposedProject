package com.ml.xposedproject.test

import android.util.Log

/**
 * Author: Menglong Ma
 * Email: mml2015@126.com
 * Date: 4/27/21 11:51 AM
 * Description: This is TestObject
 * Package: com.ml.xposedproject.test
 * Project: XposedProject
 */
object TestObject {
    fun testHook(msg:String){
        Log.d(this.javaClass.simpleName,msg)
    }
    var testFiled:String = "testFiled"
}
