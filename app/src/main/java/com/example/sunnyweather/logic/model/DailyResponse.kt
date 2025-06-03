package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName
import java.util.Date


//这个是用来解析未来几天天气的数据的  直接看着彩云天气返回的内容进行一一对应即可
data class DailyResponse(val status: String, val result: Result) {

//    result请求结果
    data class Result(val daily: Daily)

//    daily
    data class Daily(val temperature: List<Temperature>, val skycon: List<Skycon>,
                     @SerializedName("life_index") val lifeIndex: LifeIndex)

//    温度
    data class Temperature(val max: Float, val min: Float)

//    Skycon
    data class Skycon(val value: String, val date: Date)

//    对应lifeIndex
    data class LifeIndex(val coldRisk: List<LifeDescription>, val carWashing: List<LifeDescription>,
                         val ultraviolet: List<LifeDescription>,
                         val dressing: List<LifeDescription>)
//    天气描述
    data class LifeDescription(val desc: String)
}