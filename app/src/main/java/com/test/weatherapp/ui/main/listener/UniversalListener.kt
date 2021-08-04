package com.test.weatherapp.ui.main.listener

/**
 * Created by GATHAIYA on 02/08/2021.
 */
interface UniversalListener {
    fun onStarted()

    fun onSuccess(response: Any?)

    fun onFailure(message: String?)
}