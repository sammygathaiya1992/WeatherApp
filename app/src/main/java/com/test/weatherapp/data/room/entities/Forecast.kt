package com.test.weatherapp.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Entity(tableName = "forecast")
data class Forecast(
    @PrimaryKey
    @ColumnInfo(name = "date_time")
    val dateTime: String,

    @ColumnInfo(name = "description")
    val description: String,

    @ColumnInfo(name = "max_temp")
    val maximumTemperature: Double
)