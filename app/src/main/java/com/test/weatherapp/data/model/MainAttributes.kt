package com.test.weatherapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by GATHAIYA on 02/08/2021.
 */
data class MainAttributes(
    @SerializedName("temp")
    @Expose
    var temp: Double? = 0.0,

    @SerializedName("feels_like")
    @Expose
    var feelsLike: Double? = 0.0,

    @SerializedName("temp_min")
    @Expose
    var minTemperature: Double? = 0.0,

    @SerializedName("temp_max")
    @Expose
    var maxTemperature: Double? = 0.0,

    @SerializedName("pressure")
    @Expose
    var pressure: Int = 0,

    @SerializedName("humidity")
    @Expose
    var humidity: Int = 0
)