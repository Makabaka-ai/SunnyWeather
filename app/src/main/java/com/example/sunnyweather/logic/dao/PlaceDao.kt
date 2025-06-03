package com.example.sunnyweather.logic.dao

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings.Global.putString
import com.example.sunnyweather.android.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.Place
import com.google.gson.Gson
import androidx.core.content.edit

object PlaceDao {

//    将place数据保存在SharedPreferences中

fun savePlace(place: Place) {
    //获取到SharedPreferences对象后去获取editor对象，进行数据添加后apply进行提交
    sharedPreferences().edit {
        putString("place", Gson().toJson(place))
    }
}

//    获取存储过的place数据
    fun getSavedPlace(): Place {
        val placeJson = sharedPreferences().getString("place", "")
//    通过Gson进行数据解析
        return Gson().fromJson(placeJson, Place::class.java)
    }


//    判断位置是否已经存储过
    fun isPlaceSaved() = sharedPreferences().contains("place")

//  获取SharedPreferences有两种，利用Context的getSharedPreferences或者activity的getSharedPreferences
//    此处用的是第一种方式  这里获取context是通过全局context进行获取的
    private fun sharedPreferences() = SunnyWeatherApplication.context.
    getSharedPreferences("sunny_weather", Context.MODE_PRIVATE)
}