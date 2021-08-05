package com.test.weatherapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by GATHAIYA on 02/08/2021.
 */
data class WeatherAttribute(
    @SerializedName("cod")
    @Expose
    var cod: Int? = 0,

    @SerializedName("visibility")
    @Expose
    var visibility: Double? = 0.0,

    @SerializedName("weather")
    @Expose
    var weatherList: ArrayList<Weather>? = ArrayList(),

    @SerializedName("main")
    @Expose
    var mainAttributes: MainAttributes? = MainAttributes(),

    @SerializedName("wind")
    @Expose
    var wind: Wind? = Wind(),

    @SerializedName("clouds")
    @Expose
    var clouds: Clouds? = Clouds(),

    @SerializedName("sys")
    @Expose
    var country: Country? = Country(),

    @SerializedName("dt_txt")
    @Expose
    var dateTime: String? = "",

    @SerializedName("name")
    @Expose
    var regionName: String? = "",

    var hasError: Boolean = false
)