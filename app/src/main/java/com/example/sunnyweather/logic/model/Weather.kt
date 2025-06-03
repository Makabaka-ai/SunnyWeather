package com.example.sunnyweather.logic.model

//用于封装Realtime和Daily对象
data class Weather(val realtime: RealtimeResponse.Realtime, val daily: DailyResponse.Daily)