package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.R

class PlaceFragment : Fragment() {

    //    获取ViewModel实例 通过懒加载的方式
    val viewModel by lazy { ViewModelProvider(this)[PlaceViewModel::class.java] }
    private lateinit var adapter: PlaceAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_place, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

//        这里也是RecyclerView的标准写法 进行Manager和Adapter的赋值操作
        var linearLayoutManager = LinearLayoutManager(activity)
        var recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.layoutManager = linearLayoutManager
        adapter = PlaceAdapter(this, viewModel.placeList)
        recyclerView.adapter = adapter

//        接下来就要对搜索框的功能进行编写
        var searchPlaceEdit = view.findViewById<EditText>(R.id.searchPlaceEdit)

//        添加EditText的监听事件，需要一个TextWatcher对象  这里用匿名对象去完成
//        注意  这里和书里的实现方式不一样
        searchPlaceEdit.addTextChangedListener(object : TextWatcher {
            @SuppressLint("NotifyDataSetChanged")
            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
//                这里进行文本改变之前的操作  预处理操作
//                这里用户还没有对文本进行改变，那么此时的RecyclerView中的子项就不应该显示出来  设置为不可见
                var imageView = view.findViewById<ImageView>(R.id.bgImageView)
                recyclerView.visibility = View.GONE
                imageView.visibility = View.VISIBLE
                viewModel.placeList.clear()  //内容清空
                adapter.notifyDataSetChanged()  //监听变化
            }

            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
//                在这里去获取用户输入的内容
                var content = s.toString()
                if (content.isNotEmpty()) {
//                    发起网络请求 去查询相关的天气数据
                    viewModel.searchPlaces(content)
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun afterTextChanged(s: Editable?) {
//                输入之后的相关操作，例如保存文本内容
                viewModel.placeLiveData.observe(viewLifecycleOwner, Observer { result ->
                    val place = result.getOrNull()
                    if (place != null) {
//                有数据那么就去将RecyclerView的子项显示出来 包括背景什么的
                        recyclerView.visibility = View.VISIBLE
                        var imageView = view.findViewById<ImageView>(R.id.bgImageView)
                        imageView.visibility = View.GONE
                        viewModel.placeList.addAll(place)
                        adapter.notifyDataSetChanged()
                    }else {
                        recyclerView.visibility = View.GONE
                        var imageView = view.findViewById<ImageView>(R.id.bgImageView)
                        imageView.visibility = View.VISIBLE
                        Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT).show()
                        result.exceptionOrNull()?.printStackTrace()
                    }
                })
            }

        })


    }
}