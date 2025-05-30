package com.example.sunnyweather.logic


import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers

//这个是仓库层，主要作用是判断请求数据从本地获取还是网络层获取，类似于缓存和数据获取的中间层，如果缓存有就去缓存拿，没有才去进行网络数据获取操作
object Repository  {

//    仓库层里的方法需要将异步获取的数据以响应式编程方式将数据反给上一层，一般用LiveData对象进行操作
//    在这里LiveData()可以自动构建LiveData对象，并且还能提供挂起函数的上下文，我们就可以去调用挂起函数
    fun searchPlaces(query: String) = liveData(Dispatchers.IO) {  //这里启动模式选择IO，会在子线程里面执行代码，因为网络请求不允许在主线程执行
        val result = try {
            val placeResponse = SunnyWeatherNetwork.searchPlaces(query)  //发起网络请求
            if (placeResponse.status == "ok") {
                val places = placeResponse.places
                Result.success(places)  //使用Result.success去进行查询结果的包装
            } else {
                Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
        } catch (e: Exception) {
            Result.failure<List<Place>>(e)
        }
//        emit函数将结果发送出去
        emit(result)
    }

}