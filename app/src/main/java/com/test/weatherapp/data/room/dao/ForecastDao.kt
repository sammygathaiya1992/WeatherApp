package com.test.weatherapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Forecast

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Dao
interface ForecastDao {
    @Query("SELECT * FROM forecast")
    fun getAllForecasts(): LiveData<List<Forecast>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertForecast(forecast: Forecast): Long
}