package com.test.weatherapp.data.repository

import android.content.Context
import androidx.lifecycle.MutableLiveData
import com.test.weatherapp.constants.Constants
import com.test.weatherapp.data.api.RestEndpoints
import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import com.test.weatherapp.utils.RetrofitUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Created by GATHAIYA on 02/08/2021.
 */
class WeatherRepository(private val context: Context) {

    fun getWeatherForecast(latitude: Double, longitude: Double): MutableLiveData<WeatherForecastList> {

        var mutableLiveData = MutableLiveData<WeatherForecastList>()

        var retrofitUtil = RetrofitUtil(context)
        retrofitUtil.retrofit(Constants.baseUrl)!!.create(RestEndpoints::class.java)
            .getWeatherForecast(latitude, longitude, Constants.apiKey,"metric").enqueue(object : Callback<WeatherForecastList> {
                override fun onFailure(call: Call<WeatherForecastList>, t: Throwable) {
                    mutableLiveData.postValue(
                        WeatherForecastList(
                            null, true
                        )
                    )
                }
                override fun onResponse(
                    call: Call<WeatherForecastList>,
                    response: Response<WeatherForecastList>
                ) {
                    val weatherForecastResponse = response.body()
                    if (response.errorBody() != null) {
                        mutableLiveData.postValue(
                            WeatherForecastList(
                                null,true
                            )
                        )
                    } else {
                        mutableLiveData.postValue(weatherForecastResponse)
                    }
                }

            })
        return mutableLiveData
    }



    fun getCurrentWeather(latitude: Double, longitude: Double): MutableLiveData<WeatherAttribute> {

        var mutableLiveData = MutableLiveData<WeatherAttribute>()

        var retrofitUtil = RetrofitUtil(context)
        retrofitUtil.retrofit(Constants.baseUrl)!!.create(RestEndpoints::class.java)
            .getCurrentWeather(latitude, longitude, Constants.apiKey,"metric").enqueue(object : Callback<WeatherAttribute> {
                override fun onFailure(call: Call<WeatherAttribute>, t: Throwable) {
                    mutableLiveData.postValue(
                        WeatherAttribute(
                            null,null, null, null, null, null,null, "","",true
                        )
                    )
                }
                override fun onResponse(
                    call: Call<WeatherAttribute>,
                    response: Response<WeatherAttribute>
                ) {
                    val weatherResponse = response.body()
                    if (response.errorBody() != null) {
                        mutableLiveData.postValue(
                            WeatherAttribute(
                                null,null, null, null, null, null,null, "","",true
                            )
                        )
                    } else {
                        mutableLiveData.postValue(weatherResponse)
                    }
                }

            })
        return mutableLiveData
    }

}