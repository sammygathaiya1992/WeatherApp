package com.test.weatherapp.data.room.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Entity(tableName = "favourites")
data class Favourites(
    @PrimaryKey
    @ColumnInfo(name = "latitude")
    val latitude: Double,

    @ColumnInfo(name = "longitude")
    val longitude: Double,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "country_name")
    val countryName: String
)