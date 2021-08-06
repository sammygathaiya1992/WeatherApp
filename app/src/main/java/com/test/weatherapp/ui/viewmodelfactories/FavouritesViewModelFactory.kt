package com.test.weatherapp.ui.viewmodelfactories

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.test.weatherapp.ui.main.viewmodel.FavouritesViewModel

class FavouritesViewModelFactory(private val context: Context) : ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return FavouritesViewModel(context) as T
    }

}