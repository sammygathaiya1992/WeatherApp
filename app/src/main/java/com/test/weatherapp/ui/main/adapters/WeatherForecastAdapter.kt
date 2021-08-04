package com.test.weatherapp.ui.main.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.test.weatherapp.R
import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import com.test.weatherapp.utils.Utility
import java.lang.Exception

/**
 * Created by GATHAIYA on 04/08/2021.
 */
class WeatherForecastAdapter(
    private val context: Context,
    private var weatherForecastList: ArrayList<WeatherAttribute>
) : RecyclerView.Adapter<WeatherForecastAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {

        val inflater = LayoutInflater.from(context)
        val view: View = inflater.inflate(R.layout.weather_item, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return weatherForecastList.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        try {
            val weatherForecastItem = weatherForecastList[position]
            holder.weekDay?.text = Utility.getDayName(weatherForecastItem.dateTime.toString())
            holder.maxTemp?.text = weatherForecastItem.mainAttributes!!.maxTemperature.toString()
            when {
                weatherForecastItem.weatherList!![0].main.equals("Clouds") -> {
                    holder.imgWeather?.setImageResource(R.drawable.partlysunny)
                }
                weatherForecastItem.weatherList!![0].main.equals("Rain") -> {
                    holder.imgWeather?.setImageResource(R.drawable.rain)
                }
                weatherForecastItem.weatherList!![0].main.equals("Clear") -> {
                    holder.imgWeather?.setImageResource(R.drawable.clear)
                }
            }




        }catch (ex: Exception){

        }

    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var weekDay: TextView? = null
        var maxTemp: TextView? = null
        var imgWeather: ImageView? = null


        init {
            weekDay = view.findViewById(R.id.tv_week_day)
            maxTemp = view.findViewById(R.id.tv_max_temp)
            imgWeather = view.findViewById(R.id.img_weather)
        }

    }

    fun addData(list: List<WeatherAttribute>) {
        weatherForecastList.addAll(list)
    }

    fun resetCustomerList() {
        weatherForecastList = ArrayList()
    }

}