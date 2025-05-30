package com.example.sunnyweather.logic.network

import com.example.sunnyweather.android.SunnyWeatherApplication
import com.example.sunnyweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query


//这个接口是用于访问城市搜索API的Retrofit接口
interface PlaceService {

//    此处发起的GET请求里面的格式按照官网的来就行  里面需要用到TOKEN
    @GET("v2/place?token=${SunnyWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query : String) : Call<PlaceResponse>   //这里就是Retrofit里的写法，返回值必须是Call 泛型用我们的PlaceResponse 返回的数据会自动解析成PlaceResponse类型
}