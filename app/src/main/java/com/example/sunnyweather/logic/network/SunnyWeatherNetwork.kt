package com.example.sunnyweather.logic.network

import android.app.Service
import okhttp3.Call
import okhttp3.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


//这个单例类是统一的网络数据源访问入口，对所有网络请求的API进行封装  也就是通过这里进行网络请求
object SunnyWeatherNetwork {

    //    获得代理对象  此处就用到了对外暴露的create方法
    private val placeService = ServiceCreator.create<PlaceService>()  //获取地方的代理对象
    private val weatherService = ServiceCreator.create(WeatherService::class.java)  //获取天气的代理对象  然后就可以去调用里面的方法去发起网络请求获取数据了

    //    发起searchPlaces的请求，这里用suspendCoroutine函数进行了简化操作 此函数只能在协程作用域内或者挂起函数内才能使用
    suspend fun searchPlaces(query: String) = placeService.searchPlaces(query)
        .await()//发起请求后，当前协程会阻塞住直到响应了请求，await里就会取出数据模型对象并且会进行resume恢复协程的进行

    //    天气网络请求  与place的写法基本一致  也是对WeatherService里的请求方法进行了封装
    suspend fun getDailyWeather(lng: String, lat: String) =
        weatherService.getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String, lat: String) =
        weatherService.getRealtimeWeather(lng, lat).await()

    //    为Call扩展了await挂起函数，所有返回值是Call类型的Retroﬁt网络请求接口就都可以直接调用await()函数
    private suspend fun <T> retrofit2.Call<T>.await(): T {
        return suspendCoroutine { continuation ->   //suspendCoroutine会挂起当前协程，而await是Call的扩展函数，拥有它的上下文 直接去enqueue就发起请求  然后去处理相应的返回结果或者是错误信息即可
            enqueue(object : retrofit2.Callback<T> {
                override fun onResponse(
                    call: retrofit2.Call<T?>,
                    response: Response<T?>
                ) {
                    val body = response.body()  //取出数据模型
                    if (body != null) continuation.resume(body)  //有数据 那么就恢复协程
                    else continuation.resumeWithException(
                        RuntimeException("response body is null")
                    )
                }
                override fun onFailure(call: retrofit2.Call<T?>, t: Throwable) {
                    continuation.resumeWithException(t)  //请求失败将异常也传进去
                }
            })
        }
    }

}