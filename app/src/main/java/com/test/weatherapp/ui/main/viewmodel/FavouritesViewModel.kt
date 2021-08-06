package com.test.weatherapp.ui.main.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.test.weatherapp.data.room.database.WeatherDB
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.ui.main.listener.UniversalListener
import kotlinx.coroutines.launch

/**
 * Created by GATHAIYA on 02/08/2021.
 */
class FavouritesViewModel(private val context: Context) : ViewModel() {
    var listener: UniversalListener? = null
    private var favouritesData: LiveData<List<Favourites>>? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0
    private val db = Room.databaseBuilder(
        context,
        WeatherDB::class.java, "weather-app.db"
    ).fallbackToDestructiveMigration()
        .build()

    fun getFavourites(){
        viewModelScope.launch {
            listener?.onStarted()
            favouritesData = db.favouritesDao().getAllFavourites()
            listener?.onSuccess(favouritesData)
        }
    }


    fun returnFavouritesData(): LiveData<List<Favourites>>?{
        return  favouritesData
    }

}