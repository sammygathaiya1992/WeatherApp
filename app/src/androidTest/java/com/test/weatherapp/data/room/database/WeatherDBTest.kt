package com.test.weatherapp.data.room.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.test.weatherapp.data.room.dao.FavouritesDao
import com.test.weatherapp.data.room.dao.WeatherDao
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Weather
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.IOException

/**
 * Created by GATHAIYA on 05/08/2021.
 */
@RunWith(AndroidJUnit4::class)
class WeatherDBTest{
    private lateinit var weatherDao: WeatherDao
    private lateinit var favouritesDao: FavouritesDao
    private lateinit var db: WeatherDB

    @Before
    fun createDatabase(){
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(
            context, WeatherDB::class.java).build()
        weatherDao = db.weatherDao()
        favouritesDao= db.favouritesDao()
    }
    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    fun writeAndReadWeather() = runBlocking {
        val weather=  Weather(10.0,"Clouds",8.0,15.0)
        weatherDao.insertWeatherDetails(weather)
        val weatherList= weatherDao.getAllWeatherRecords()
        assertEquals(weatherList[0].currentTemp, weather.currentTemp,0.0)
    }

    @Test
    fun writeAndReadFavourite() = runBlocking {
        val favourites=  Favourites(0.11484,12.21554,"Nairobi","Kenya")
        favouritesDao.insertFavourite(favourites)
        val favouriteList= favouritesDao.getAll()
        assertEquals(favouriteList[0].countryName, favourites.countryName)
    }


}