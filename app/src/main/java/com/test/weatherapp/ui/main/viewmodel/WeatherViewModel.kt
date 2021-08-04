package com.test.weatherapp.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import com.test.weatherapp.data.repository.WeatherRepository
import com.test.weatherapp.ui.main.listener.UniversalListener

/**
 * Created by GATHAIYA on 02/08/2021.
 */
class WeatherViewModel(private val context: Context) : ViewModel() {
    var listener: UniversalListener? = null
    private val weatherRepository = WeatherRepository(context)
    private var weatherForecastData = MutableLiveData<WeatherForecastList>()
    private var currentWeatherData = MutableLiveData<WeatherAttribute>()
    var latitude: Double = 0.0
    var longitude: Double = 0.0


    fun getWeatherForecast() {
        listener?.onStarted()
        weatherForecastData = weatherRepository.getWeatherForecast(latitude, longitude)
        listener?.onSuccess(weatherForecastData)
    }

    fun getCurrentWeather() {
        listener?.onStarted()
        currentWeatherData = weatherRepository.getCurrentWeather(latitude, longitude)
        listener?.onSuccess(currentWeatherData)
    }

    fun returnWeatherForecastData(): MutableLiveData<WeatherForecastList> {
        return weatherForecastData
    }

    fun returnCurrentWeatherData(): MutableLiveData<WeatherAttribute> {
        return currentWeatherData
    }
}