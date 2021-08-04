package com.test.weatherapp.data.api


import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import retrofit2.Call
import retrofit2.http.*

/**
 * Created by GATHAIYA on 02/08/2021.
 */
interface RestEndpoints {

    @GET("/data/2.5/forecast")
    fun getWeatherForecast(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("appid") appId: String?,
        @Query("units") unit: String?,
    ): Call<WeatherForecastList>

    @GET("/data/2.5/weather")
    fun getCurrentWeather(
        @Query("lat") latitude: Double?,
        @Query("lon") longitude: Double?,
        @Query("appid") appId: String?,
        @Query("units") unit: String?,
    ): Call<WeatherAttribute>
}