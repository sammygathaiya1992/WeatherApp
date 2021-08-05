package com.test.weatherapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/**
 * Created by GATHAIYA on 02/08/2021.
 */
data class Country (
    @SerializedName("country")
    @Expose
    var countryName:String? = ""
)