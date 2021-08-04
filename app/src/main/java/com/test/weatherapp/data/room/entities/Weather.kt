package com.test.weatherapp.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Entity(tableName = "weather")
data class Weather(
    @PrimaryKey
    @ColumnInfo(name = "current_temp")
    val currentTemp: Double,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "min_temp")
    val minimumTemperature: Double,

    @ColumnInfo(name = "max_temp")
    val maximumTemperature: Double
)