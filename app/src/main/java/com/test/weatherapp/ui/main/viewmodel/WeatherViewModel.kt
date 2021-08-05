package com.test.weatherapp.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import com.test.weatherapp.data.repository.WeatherRepository
import com.test.weatherapp.data.room.database.WeatherDB
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Weather
import com.test.weatherapp.ui.main.listener.UniversalListener
import kotlinx.coroutines.launch

/**
 * Created by GATHAIYA on 02/08/2021.
 */
class WeatherViewModel(private val context: Context) : ViewModel() {
    var listener: UniversalListener? = null
    private val weatherRepository = WeatherRepository(context)
    private var weatherForecastData = MutableLiveData<WeatherForecastList>()
    private var currentWeatherData = MutableLiveData<WeatherAttribute>()
    private var weatherData: LiveData<List<Weather>>? = null
    private var favouritesData: LiveData<List<Favourites>>? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private val db = Room.databaseBuilder(
        context,
        WeatherDB::class.java, "weather-app.db"
    ).fallbackToDestructiveMigration()
        .build()


    fun getWeatherForecast() {
        listener?.onStarted()
        weatherForecastData = weatherRepository.getWeatherForecast(latitude, longitude)
        listener?.onSuccess(weatherForecastData)
    }

    fun getWeatherDetails() {
        viewModelScope.launch {
            listener?.onStarted()
            weatherData = db.weatherDao().getWeatherDetails()
            listener?.onSuccess(weatherData)
        }

    }

    fun getFavourites(){
        viewModelScope.launch {
            listener?.onStarted()
            favouritesData = db.favouritesDao().getAllFavourites()
            listener?.onSuccess(favouritesData)
        }
    }

    fun getCurrentWeather() {
        listener?.onStarted()
        currentWeatherData = weatherRepository.getCurrentWeather(latitude, longitude)
        listener?.onSuccess(currentWeatherData)
    }
    fun returnFavouritesData(): LiveData<List<Favourites>>?{
        return  favouritesData
    }

    fun returnWeatherForecastData(): MutableLiveData<WeatherForecastList> {
        return weatherForecastData
    }

    fun returnCurrentWeatherData(): MutableLiveData<WeatherAttribute> {
        return currentWeatherData
    }

    fun returnWeatherData(): LiveData<List<Weather>>?{
        return  weatherData
    }
}