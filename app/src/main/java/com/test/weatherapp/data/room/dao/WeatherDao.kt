package com.test.weatherapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Weather

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Dao
interface WeatherDao {
    @Query("SELECT * FROM weather LIMIT 1")
    fun getWeatherDetails(): LiveData<List<Weather>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertWeatherDetails(weather: Weather): Long
}