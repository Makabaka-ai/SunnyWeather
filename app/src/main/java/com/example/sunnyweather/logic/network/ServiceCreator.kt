package com.example.sunnyweather.logic.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

//Retrofit构建器 可以在这里进行根路径的定义、动态代理对象的创建
object ServiceCreator {
//    发出请求后拼接的基本路径
    private const val BASE_URL = "https://api.caiyunapp.com/"

//    构建Retrofit对象 将基本路径传入
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())  //解析数据时的转换库  用Gson的转换库
        .build()
    fun <T> create(serviceClass: Class<T>): T = retrofit.create(serviceClass)  //创建动态代理对象，更方便的去调用方法

    inline fun <reified T> create(): T = create(T::class.java)  //对外暴露的create方法，用于快速获取某个类型的动态代理对象  用什么类型传什么类型
}