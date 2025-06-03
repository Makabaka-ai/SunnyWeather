package com.example.sunnyweather.logic.model

import com.google.gson.annotations.SerializedName

//这里声明的三个都是根据彩云科技里获取到的JSON数据来进行设置的  调用那边的接口之后就会返回这种格式的数据  用于后续的解析
data class PlaceResponse(val status: String, val places: List<Place>)

data class Place(
//    place对象包含地名以及经纬度
    val name: String, val location: Location,
//    这个注解让JSON字段和kotlin字段建立映射关系
    @SerializedName("formatted_address") val address: String
)

data class Location(val lng: String, val lat: String)