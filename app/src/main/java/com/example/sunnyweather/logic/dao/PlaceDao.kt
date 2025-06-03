package com.example.sunnyweather.logic.dao

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Global.putString
import com.example.sunnyweather.android.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson
import androidx.core.content.edit

//数据交互的地方 比如存入文件或者从文件读取数据
object PlaceDao {

    //    将place数据保存在SharedPreferences中
    fun savePlace(place: Place) {
        //获取到SharedPreferences对象后去获取editor对象，进行数据添加后apply进行提交
//    这里调用的edit是SharedPreferences的扩展方法，里面最后会自动进行apply提交操作
        sharedPreferences().edit {
            putString("place", Gson().toJson(place))
        }
    }

    //    获取存储过的place数据
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
//    通过Gson进行数据解析  Gson解析最简单 直接fromJson将数据传入 将要解析成的对象传入即可
        return Gson().fromJson(placeJson, Place::class.java)
    }


    //    判断位置是否已经存储过  也是一个定义好的方法  用于判断是否包含某个key对应的value
    fun isPlaceSaved() = sharedPreferences().contains("place")

    //  获取SharedPreferences有两种，利用Context的getSharedPreferences或者activity的getSharedPreferences
//    此处用的是第一种方式  这里获取context是通过全局context进行获取的
    private fun sharedPreferences() =
        SunnyWeatherApplication.context.getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}