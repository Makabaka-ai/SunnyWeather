package com.example.sunnyweather.ui.place

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Transformation
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sunnyweather.MainActivity
import com.example.sunnyweather.R
import com.example.sunnyweather.ui.weather.WeatherActivity

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

//        判断是否已经有存储的城市  有的话就直接从SharedPreferences中获取数据展示  不需要再去发送网络请求
//        如果只写到这里就会产生只能看某一个城市的天气的问题  就需要有切换城市的操作
//        这里的两个判断操作 一个是判断是否存储 一个是判断当前是不是MainActivity  防止出现无限循环的情况
//        现在placeFragment已经嵌在weatherActivity里，就会产生无限跳转的情况

//        这里调用的viewModel的判断方法 实际上还是调用的Dao里面的方法
        if (viewModel.isPlaceSaved() && activity is MainActivity) {
            val place = viewModel.getSavedPlace()
            val intent = Intent(context, WeatherActivity::class.java).apply {
                putExtra("location_lng", place.location.lng)
                putExtra("location_lat", place.location.lat)
                putExtra("place_name", place.name)
            }
            startActivity(intent)
            activity?.finish()
            return
        }

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
                    } else {
//                        没有数据就不展示
                        recyclerView.visibility = View.GONE
                        var imageView = view.findViewById<ImageView>(R.id.bgImageView)
                        imageView.visibility = View.VISIBLE
                        Toast.makeText(activity, "未能查询到任何地点", Toast.LENGTH_SHORT)
                            .show()
                        result.exceptionOrNull()?.printStackTrace()
                    }
                })
            }
        })
    }
}