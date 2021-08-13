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
    private val list = mutableListOf<DataItem>().apply {
        add(DataItem("一加多开", Config.KEYS.ENABLE_O_P_M_A))
        add(DataItem("开启小妖精美化hook", Config.KEYS.ENABLE_XYJMH))
        add(DataItem("开启抖音hook", Config.KEYS.ENABLE_DY))
        add(DataItem("开启红杏hook", Config.KEYS.ENABLE_HX))
        add(DataItem("开启佛跳墙hook", Config.KEYS.ENABLE_FTQ))
        add(DataItem("开启导出dex", Config.KEYS.ENABLE_EXPORT))
        add(DataItem("开启嘿咻hook", Config.KEYS.ENABLE_HXMH))
        add(DataItem("开启星辰影视hook", Config.KEYS.ENABLE_XCYS))
        add(DataItem("开启葫芦娃hook", Config.KEYS.ENABLE_HLW))
    }
    private lateinit var mAdapter: RecyclerView.Adapter<ViewHolder>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        initHookView()
        setTitle("${title}:${BuildConfig.buildTime}")
    }

    private fun initHookView() {
        viewBinding.tvHookResult.text = getHook()
        viewBinding.tvHookResult.isChecked = isActive()
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
                        Config.setBool(this@MainActivity, dataItem.key, isChecked)
                    }
                    isChecked = Config.getBool(this@MainActivity, dataItem.key)
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
        return "未hook"
    }

    private fun isActive(): Boolean = false
    private fun hookMe(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    inner class ViewHolder(val binding: ItemHookListBinding) : RecyclerView.ViewHolder(binding.root)
}