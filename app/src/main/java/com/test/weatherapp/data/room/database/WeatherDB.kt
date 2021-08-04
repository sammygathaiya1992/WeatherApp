package com.test.weatherapp.data.room.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.test.weatherapp.data.room.dao.FavouritesDao
import com.test.weatherapp.data.room.dao.ForecastDao
import com.test.weatherapp.data.room.dao.WeatherDao
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Forecast
import com.test.weatherapp.data.room.entities.Weather


/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Database(entities = [Favourites::class, Forecast::class, Weather::class], version = 1)
abstract class WeatherDB: RoomDatabase() {

    private var INSTANCE: WeatherDB? = null

    open fun destroyInstance() {
        INSTANCE = null
    }

    companion object {
        @Volatile private var instance: WeatherDB? = null
        private val LOCK = Any()

        operator fun invoke(context: Context)= instance ?: synchronized(LOCK){
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(context,
            WeatherDB::class.java, "weather-app.db")
            .build()
    }

    abstract fun favouritesDao(): FavouritesDao

    abstract fun forecastDao(): ForecastDao

    abstract fun weatherDao(): WeatherDao
}