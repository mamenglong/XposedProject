package com.ml.xposedproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.viewbinding.ViewBinding
import com.ml.xposedproject.databinding.ActivityMainBinding
import com.ml.xposedproject.tools.Config
import dalvik.system.DexClassLoader
import dalvik.system.PathClassLoader
import de.robv.android.xposed.callbacks.XC_LoadPackage
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
         initHookView()
    }
   private fun initHookView(){
       viewBinding.tvHookResult.text = getHook()
       viewBinding.tvHookResult.isChecked = isActive()
   }
    override fun onResume() {
        super.onResume()
        initView()
    }
    private fun initView() {
        viewBinding.swOpenDy.isChecked = Config.getBool(this,Config.KEYS.ENABLE_DY)
        viewBinding.swOpenXyjmh.isChecked = Config.getBool(this,Config.KEYS.ENABLE_XYJMH)
        viewBinding.swOpenXyjmh.setOnCheckedChangeListener { buttonView, isChecked ->
            Config.setBool(this,Config.KEYS.ENABLE_XYJMH,isChecked)
        }
        viewBinding.swOpenDy.setOnCheckedChangeListener { buttonView, isChecked ->
            Config.setBool(this,Config.KEYS.ENABLE_DY,isChecked)
        }
    }

    private fun getHook():String{
         return "未hook"
    }
    private fun isActive():Boolean = false
    private fun hookMe(msg:String){
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show()
    }
}