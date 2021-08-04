package com.test.weatherapp.data.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

/**
 * Created by GATHAIYA on 02/08/2021.
 */
data class Weather(
    @SerializedName("id")
    @Expose
    var id: String? = "",

    @SerializedName("main")
    @Expose
    var main: String? = "",

    @SerializedName("description")
    @Expose
    var description: String? = "",

    @SerializedName("icon")
    @Expose
    var icon: String? = ""
) : Serializable