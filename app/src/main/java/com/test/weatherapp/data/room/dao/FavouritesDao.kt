package com.test.weatherapp.data.room.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.test.weatherapp.data.room.entities.Favourites

/**
 * Created by GATHAIYA on 02/08/2021.
 */
@Dao
interface FavouritesDao {
    @Query("SELECT * FROM favourites")
    fun getAllFavourites(): LiveData<List<Favourites>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertFavourite(favourites: Favourites): Long
}