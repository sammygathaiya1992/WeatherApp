package com.test.weatherapp.ui.main.view

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.test.weatherapp.R
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.ui.main.adapters.FavouritesAdapter
import com.test.weatherapp.ui.main.listener.UniversalListener
import com.test.weatherapp.ui.main.viewmodel.FavouritesViewModel
import com.test.weatherapp.ui.viewmodelfactories.FavouritesViewModelFactory
import com.test.weatherapp.utils.Utility
import kotlinx.android.synthetic.main.content_favourites_locations.*

class FavouritesLocationsList : AppCompatActivity(), UniversalListener {
    private var context = this
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var adapter: FavouritesAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourites_locations)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        initResources()
        getAllFavourites()
    }

    private fun initResources() {
        favouritesViewModel =
            ViewModelProviders.of(this, FavouritesViewModelFactory(this))
                .get(FavouritesViewModel::class.java)
        favouritesViewModel.listener = this

        rv_favourites.layoutManager = LinearLayoutManager(this)
        adapter = FavouritesAdapter(context, arrayListOf())
        rv_favourites.adapter = adapter

    }

    private fun getAllFavourites() {
        favouritesViewModel.getFavourites()
    }




    private fun renderFavouritesList(favourites: List<Favourites>) {
        adapter.addData(favourites)
        adapter.notifyDataSetChanged()
    }
    override fun onStarted() {
        favouritesViewModel.returnFavouritesData()?.observe(this, {

        })
    }

    override fun onSuccess(response: Any?) {
        var res = response as LiveData<List<Favourites>>
        res?.observe(this, {
            Utility.hideProgressBar(this)
            if (it.isNotEmpty()) {
                renderFavouritesList(it)
            }else{
                this.onFailure(getString(R.string.no_favourites))
            }
        })
    }

    override fun onFailure(message: String?) {
        Utility.hideProgressBar(this)
        Utility.showCustomErrorDialog(context, message, getString(R.string.failed))
    }
}