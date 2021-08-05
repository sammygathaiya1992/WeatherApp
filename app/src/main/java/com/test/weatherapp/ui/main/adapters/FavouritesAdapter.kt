package com.test.weatherapp.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp.R
import com.test.weatherapp.data.room.entities.Favourites
import java.lang.Exception

/**
 * Created by GATHAIYA on 04/08/2021.
 */
class FavouritesAdapter(
    private val context: Context,
    private var favouritesList: ArrayList<Favourites>
) : RecyclerView.Adapter<FavouritesAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.favourite_location_cell, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return favouritesList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val favouriteItem = favouritesList[position]
            holder.number?.text= (position+1).toString()+"."
            holder.locationName?.text = favouriteItem.name
            holder.countryName?.text = context.getString(R.string.country_name,favouriteItem.countryName)
            holder.latitude?.text = context.getString(R.string.latitude,""+favouriteItem.latitude)
            holder.longitude?.text = context.getString(R.string.longitude,""+favouriteItem.longitude)
        }catch (ex: Exception){

        }

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var number: TextView? = null
        var locationName: TextView? = null
        var countryName: TextView? = null
        var latitude: TextView? = null
        var longitude: TextView? = null

        init {
            number = view.findViewById(R.id.number)
            locationName = view.findViewById(R.id.tv_location_name)
            countryName = view.findViewById(R.id.tv_country_name)
            latitude = view.findViewById(R.id.tv_latitude)
            longitude = view.findViewById(R.id.tv_longitude)
        }

    }

    fun addData(list: List<Favourites>) {
        favouritesList.addAll(list)
    }

}