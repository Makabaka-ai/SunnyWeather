package com.example.sunnyweather.android

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

//全局获取context  还需要在manifest里进行声明
class SunnyWeatherApplication : Application() {
    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context  //警告是可能会出现内存溢出的问题  添加注解去解决
        const val TOKEN = "8bsq9ZN3Agprv5C0"  //这个TOKEN是用来访问彩云科技的 调用接口什么的需要用到
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}