package com.test.weatherapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by GATHAIYA on 02/08/2021.
 */
data class Wind(
    @SerializedName("speed")
    @Expose
    var speed: Double? = 0.0,

    @SerializedName("deg")
    @Expose
    var deg:Int? = 0
)