package com.example.sunnyweather.logic


import androidx.lifecycle.liveData
import com.example.sunnyweather.logic.dao.PlaceDao
import com.example.sunnyweather.logic.model.Place
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.network.SunnyWeatherNetwork
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlin.coroutines.CoroutineContext

//这个是仓库层，主要作用是判断请求数据从本地获取还是网络层获取，类似于缓存和数据获取的中间层，如果缓存有就去缓存拿，没有才去进行网络数据获取操作
object Repository {

    //    仓库层里的方法需要将异步获取的数据以响应式编程方式将数据反给上一层，一般用LiveData对象进行操作
//    在这里LiveData()可以自动构建LiveData对象，并且还能提供挂起函数的上下文，我们就可以去调用挂起函数
    fun searchPlaces(query: String) =
        fire(Dispatchers.IO) {  //这里启动模式选择IO，会在子线程里面执行代码，因为网络请求不允许在主线程执行
                val placeResponse = SunnyWeatherNetwork.searchPlaces(query)  //发起网络请求
                if (placeResponse.status == "ok") {
                    val places = placeResponse.places
                    Result.success(places)  //使用Result.success去进行查询结果的包装
                } else {
                    Result.failure(RuntimeException("response status is${placeResponse.status}"))
                }

//        emit函数将结果发送出去
//            emit(result)
        }


//    在这里将请求天气信息和未来天气的两个方法进行了封装，发起一次请求就可以将两个结果都获取到  减少网络请求的次数
    fun refreshWeather(lng: String, lat: String) = fire(Dispatchers.IO) {

//            由于这里是两个请求和结果，可以通过async去进行并发执行 通过await保证都执行成功后才会往下走
//            而async必须在协程作用域内才可以使用，因此这里也用到了coroutineScope去构建
            coroutineScope {
//                发起天气信息请求
                val deferredRealtime = async {
                    SunnyWeatherNetwork.getRealtimeWeather(lng, lat)
                }
//                发起未来天气信息请求
                val deferredDaily = async {
                    SunnyWeatherNetwork.getDailyWeather(lng, lat)
                }

                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok") {
                    val weather = Weather(
                        realtimeResponse.result.realtime,
                        dailyResponse.result.daily
                    )
                    Result.success(weather)
                } else {
                    Result.failure(
                        RuntimeException(
                            "realtime response status is ${realtimeResponse.status}" +
                                    "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
    }


//    这里可以对上述每个请求中可能存在的异常问题的try-catch进行一个简化，统一在这里进行操作  简化代码
    private fun <T> fire(context: CoroutineContext, block: suspend () -> Result<T>) =
        liveData<Result<T>>(context) {
            val result = try {
                block()
            } catch (e: Exception) {
                Result.failure<T>(e)
            }
            emit(result)
        }


//    这三个方法就是用于保存查询过的数据的  调用的就是dao里的方法
    fun savePlace(place: Place) = PlaceDao.savePlace(place)
    fun getSavedPlace() = PlaceDao.getSavedPlace()
    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}