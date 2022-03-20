package com.ml.xposedproject

import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginLeft
import androidx.core.view.marginRight
import androidx.recyclerview.widget.RecyclerView
import com.ml.xposedproject.databinding.ActivityMainBinding
import com.ml.xposedproject.databinding.ItemHookListBinding
import com.ml.xposedproject.hook.HookFactory
import com.ml.xposedproject.taichi.TaiChiUtil
import com.ml.xposedproject.test.TestFiled
import com.ml.xposedproject.test.TestObject
import com.ml.xposedproject.tools.Config
import dagger.hilt.android.AndroidEntryPoint

data class DataItem(
    val label: String,
    val key: String,
    val id: Int = System.currentTimeMillis().toInt()
)
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding
    private val list =  HookFactory.mapDataItem()
    private lateinit var mAdapter: RecyclerView.Adapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initHookView()
        setTitle("${title}:${BuildConfig.buildTime}")
    }

    private fun initHookView() {
        Config.initMain(this)
        viewBinding.tvHookResult.text = getHook()
        viewBinding.tvHookResult.isChecked = isActive()
        taichi {
            viewBinding.tvActiveTaichi.visibility = View.VISIBLE
            viewBinding.tvActiveTaichi.isChecked = TaiChiUtil.isExpModuleActive(this)
            viewBinding.tvActiveTaichi.setOnClickListener {
                TaiChiUtil.activeModule(this,packageName)
            }
        }
        initView()
        viewBinding.btnTest.setOnClickListener {
            TestObject.testHook("${System.currentTimeMillis()}")
            val f = TestFiled()
            f.isVip = f.isVip
            if (f.isVip){
                //do something
            }else{

            }
            f.testFiled ="sssssss"
            Log.d("MainActivity", f.testFiled)
            TestObject.testFiled = "ssss"
            Log.d("MainActivity", TestObject.testFiled)


        }
    }


    override fun onResume() {
        super.onResume()
    }

    private fun initView() {
        mAdapter = object : RecyclerView.Adapter<ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
                val binding = ItemHookListBinding.inflate(layoutInflater, parent, false)
                return ViewHolder(binding)
            }

            override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                val dataItem = list[position]
                holder.binding.mSwitchCompat.apply {
                    text = dataItem.label
                    setOnCheckedChangeListener { buttonView, isChecked ->
                        if (isActive()) {
                            Config.setBool(this@MainActivity,dataItem.key, isChecked)
                        }else{
                            if (BuildConfig.DEBUG){
                                Config.setBool(this@MainActivity,dataItem.key, isChecked)
                            }
                            showToast("请先激活插件")
                        }
                    }
                    isChecked = Config.getBool(this@MainActivity,dataItem.key)
                }
            }

            override fun getItemCount(): Int {
                return list.size
            }


        }
        viewBinding.mRecyclerView.apply {
            adapter = mAdapter
            addItemDecoration(object :RecyclerView.ItemDecoration(){
                override fun getItemOffsets(
                    outRect: Rect,
                    view: View,
                    parent: RecyclerView,
                    state: RecyclerView.State
                ) {
                    super.getItemOffsets(outRect, view, parent, state)
                    val pos = parent.getChildAdapterPosition(view)
                    if (pos%2==1) {
                        outRect.left = viewBinding.tvHookResult.marginLeft
                        outRect.right = viewBinding.tvHookResult.marginRight/2
                    }else{
                        outRect.left = viewBinding.tvHookResult.marginLeft/2
                        outRect.right = viewBinding.tvHookResult.marginRight
                    }
                }
            })
        }
    }

    private fun getHook(): String {
        return if (isActive()) "模块已启用" else "未hook"
    }

    private fun isActive(): Boolean = false
    
    inner class ViewHolder(val binding: ItemHookListBinding) : RecyclerView.ViewHolder(binding.root)
}