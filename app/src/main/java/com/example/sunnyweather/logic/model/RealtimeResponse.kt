package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName


//跟place的相同，都是去进行数据解析的  格式就看彩云天气的返回值即可
data class RealtimeResponse(val status: String, val result: Result) {
    data class Result(val realtime: Realtime)

    data class Realtime(val skycon: String, val temperature: Float,
                        @SerializedName("air_quality") val airQuality: AirQuality)

    data class AirQuality(val aqi: AQI)

    data class AQI(val chn: Float)
}