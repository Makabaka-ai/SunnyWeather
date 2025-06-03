package com.example.sunnyweather.ui.weather

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.sunnyweather.R
import com.example.sunnyweather.databinding.ActivityWeatherBinding
import com.example.sunnyweather.logic.model.Weather
import com.example.sunnyweather.logic.model.getSky
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.getValue

class WeatherActivity : AppCompatActivity() {

    val viewModel by lazy { ViewModelProvider(this)[WeatherViewModel::class.java] }
    lateinit var binding: ActivityWeatherBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWeatherBinding.inflate(layoutInflater)
        setContentView(binding.root)

//        判断经纬度和城市名称是否为空
        if (viewModel.locationLng.isEmpty()) {
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()) {
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()) {
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
//        observe观察数据的变化情况
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null) {
                showWeatherInfo(weather)
            } else {
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            binding.swipeRefresh.isRefreshing = false  //请求结束后将下拉刷新的进度条隐藏起来 表示下拉刷新结束
        })
        binding.swipeRefresh.setColorSchemeResources(R.color.teal_200)   //设置下拉刷新进度条的颜色
        refreshWeather()  //将下拉刷新的进度条设置为可见
        binding.swipeRefresh.setOnRefreshListener {  //这里是一个下拉刷新监听器  触发了下拉刷新操作后就去刷新天气内容
            refreshWeather()
        }
//        数据发生变化后进行刷新
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)


//        对于切换城市的实现部分
        binding.weatherLayout.findViewById<Button>(R.id.navBtn).setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)  //开启滑动菜单
        }
        binding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener{
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}

            override fun onDrawerClosed(drawerView: View) {
//                这一步主要是在滑动菜单隐藏之后  将输入法给隐藏起来  不至于搜索完了输入法还一直存在
                val manager = getSystemService(INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }

            override fun onDrawerStateChanged(newState: Int) {}

        })

    }

//    展示天气信息  分别对三个布局内容进行数据的填充
    private fun showWeatherInfo(weather: Weather) {
        var forecastLayout = binding.weatherLayout.findViewById<LinearLayout>(R.id.forecastLayout)
        binding.weatherLayout.findViewById<TextView>(R.id.placeName).text = viewModel.placeName
        val realtime = weather.realtime
        val daily = weather.daily

//      填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()} ℃"
        binding.weatherLayout.findViewById<TextView>(R.id.currentTemp).text = currentTempText
        binding.weatherLayout.findViewById<TextView>(R.id.currentSky).text = getSky(realtime.skycon).info
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        binding.weatherLayout.findViewById<TextView>(R.id.currentAQI).text = currentPM25Text
        binding.weatherLayout.findViewById<RelativeLayout>(R.id.nowLayout).setBackgroundResource(getSky(realtime.skycon).bg)

//      填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days) {
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                forecastLayout, false)
            val dateInfo: TextView = view.findViewById(R.id.dateInfo)
            val skyIcon: ImageView = view.findViewById(R.id.skyIcon)
            val skyInfo: TextView = view.findViewById(R.id.skyInfo)
            val temperatureInfo: TextView = view.findViewById(R.id.temperatureInfo)
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()} ℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }

//      填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        binding.weatherLayout.findViewById<TextView>(R.id.coldRiskText).text = lifeIndex.coldRisk[0].desc
        binding.weatherLayout.findViewById<TextView>(R.id.dressingText).text = lifeIndex.dressing[0].desc
        binding.weatherLayout.findViewById<TextView>(R.id.ultravioletText).text = lifeIndex.ultraviolet[0].desc
        binding.weatherLayout.findViewById<TextView>(R.id.carWashingText).text = lifeIndex.carWashing[0].desc
        binding.weatherLayout.visibility = View.VISIBLE
    }


    fun refreshWeather() {
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
        binding.swipeRefresh.isRefreshing = true
    }
}