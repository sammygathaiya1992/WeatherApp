package com.test.weatherapp.ui.main.view

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.*
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.ActivityCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.gms.location.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.test.weatherapp.R
import com.test.weatherapp.data.model.WeatherAttribute
import com.test.weatherapp.data.model.WeatherForecastList
import com.test.weatherapp.data.room.database.WeatherDB
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.data.room.entities.Weather
import com.test.weatherapp.ui.main.adapters.WeatherForecastAdapter
import com.test.weatherapp.ui.main.listener.UniversalListener
import com.test.weatherapp.ui.main.viewmodel.WeatherViewModel
import com.test.weatherapp.ui.viewmodelfactories.WeatherViewModelFactory
import com.test.weatherapp.utils.Utility
import com.test.weatherapp.utils.Utility.showSuccessToast
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity(), UniversalListener {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val permissionRequest = 99
    private var context = this
    private lateinit var weatherViewModel: WeatherViewModel
    private var isGetCurrentWeather = true
    private var latitude = 0.0
    private var longitude = 0.0
    private lateinit var adapter: WeatherForecastAdapter
    private var weatherInfo = WeatherAttribute()
    private var countryName = ""
    private var countryCode = ""
    private var adminArea = ""
    private lateinit var db: WeatherDB
    private var isGetLocalData = false
    private var mBottomSheetDialog: BottomSheetDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setUpToolBar()
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        initResources()
        // request all permissions
        requestPermissions()
        checkIfGpsIsEnabled()
    }

    private fun getExtraLocationDetails(lat: Double, lon: Double) {
        val geoCoder = Geocoder(this, Locale.getDefault())
        var address = geoCoder.getFromLocation(lat, lon, 1)
        countryCode = address[0].countryCode
        countryName = address[0].countryName
        adminArea = address[0].adminArea
    }

    private fun saveFavouriteLocation() {
        var favourites = Favourites(latitude, longitude, weatherInfo.regionName!!, countryName)
        db.favouritesDao().insertFavourite(favourites)
    }

    private fun saveCurrentWeatherDetails() {
        try {
            var weather = Weather(
                weatherInfo.mainAttributes?.temp!!,
                weatherInfo.weatherList!![0].main.toString(),
                weatherInfo.mainAttributes?.minTemperature!!,
                weatherInfo.mainAttributes?.maxTemperature!!
            )
            db.weatherDao().deleteAll()
            db.weatherDao().insertWeatherDetails(weather)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

    }

    private fun setUpToolBar() {
        toolbar.setNavigationIcon(R.drawable.ic_menu)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
    }

    private fun initResources() {
        db = Room.databaseBuilder(
            context,
            WeatherDB::class.java, "weather-app.db"
        ).fallbackToDestructiveMigration()
            .build()
        weatherViewModel =
            ViewModelProviders.of(this, WeatherViewModelFactory(this))
                .get(WeatherViewModel::class.java)
        weatherViewModel.listener = this

        rv_weather_forecast.layoutManager = LinearLayoutManager(this)
        adapter = WeatherForecastAdapter(context, arrayListOf())
        rv_weather_forecast.adapter = adapter


    }

    private fun requestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), permissionRequest
            )
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (applicationContext != null) {
            if (requestCode == permissionRequest) {
                var allGranted = true
                for (res in grantResults.indices) {
                    val i = grantResults[res]
                    allGranted = i == PackageManager.PERMISSION_GRANTED
                }
                if (allGranted) {
                    if (ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                            this,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        ) != PackageManager.PERMISSION_GRANTED
                    ) {
                        requestPermissions()
                        return
                    }
                    fusedLocationClient.lastLocation.addOnSuccessListener {
                        if (it != null) {
                            if (Utility.isNetWorkAvailable(context)) {
                                latitude = it.latitude
                                longitude = it.longitude
                                getExtraLocationDetails(it.latitude, it.longitude)
                                getCurrentWeatherDetails(it.latitude, it.longitude)
                            } else {

                                isGetCurrentWeather = false
                                isGetLocalData = true
                                //load locally saved details
                                getLocallySavedWeatherDetails()

                            }
                        } else {
                            isGetCurrentWeather = false
                            isGetLocalData = true
                            getLocallySavedWeatherDetails()
                        }
                    }


                } else {
                    runOnUiThread {
                        grantPermissionsDialog()
                    }

                }
            }
        }
    }

    private fun getLocallySavedWeatherDetails() {
        weatherViewModel.getWeatherDetails()
    }

    private fun getWeatherForecastDetails(lat: Double, lon: Double) {
        weatherViewModel.latitude = lat
        weatherViewModel.longitude = lon
        weatherViewModel.getWeatherForecast()
    }

    private fun getCurrentWeatherDetails(lat: Double, lon: Double) {
        weatherViewModel.latitude = lat
        weatherViewModel.longitude = lon
        weatherViewModel.getCurrentWeather()
    }


    private fun checkIfGpsIsEnabled() {
        val lm = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var gpsEnabled = false
        var networkEnabled = false
        try {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER)
        } catch (ex: Exception) {
        }

        try {
            networkEnabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } catch (ex: Exception) {
        }
        if (!gpsEnabled && !networkEnabled) {
            //Open settings for user to switch on location
            showCustomErrorDialog()

        }
    }


    private fun displayLocallyFetchedDetails(weather: Weather) {
        tv_min.text = Utility.convertToString(weather.minimumTemperature) + "??"
        tv_current.text = Utility.convertToString(weather.currentTemp) + "??"
        tv_max.text = Utility.convertToString(weather.maximumTemperature) + "??"
        when (weather.description) {
            "Clouds" -> {
                temperature.text =
                    Utility.convertToString(weather.currentTemp) + "??" + "\n CLOUDY"
                img_weather_image.setImageResource(R.drawable.forest_cloudy)
                lyt_values.setBackgroundColor(resources.getColor(R.color.cloudy))
            }
            "Rain" -> {
                temperature.text =
                    Utility.convertToString(weather.currentTemp) + "??" + "\n RAINY"
                img_weather_image.setImageResource(R.drawable.forest_rainy)
                lyt_values.setBackgroundColor(resources.getColor(R.color.rainy))
            }
            "Clear" -> {
                temperature.text =
                    Utility.convertToString(weather.currentTemp) + "??" + "\n SUNNY"
                img_weather_image.setImageResource(R.drawable.forest_sunny)
                lyt_values.setBackgroundColor(resources.getColor(R.color.sunny))
            }
        }
    }

    private fun displayCurrentWeather(weatherAttribute: WeatherAttribute) {
        if (weatherAttribute.weatherList!!.isNotEmpty()) {
            var weatherList = weatherAttribute.weatherList
            tv_min.text =
                Utility.convertToString(weatherAttribute.mainAttributes?.minTemperature!!) + "??"
            tv_current.text = Utility.convertToString(weatherAttribute.mainAttributes?.temp!!) + "??"
            tv_max.text =
                Utility.convertToString(weatherAttribute.mainAttributes?.maxTemperature!!) + "??"
            when (weatherList!![0].main) {
                "Clouds" -> {
                    temperature.text =
                        Utility.convertToString(weatherAttribute.mainAttributes?.temp!!) + "??" + "\n CLOUDY"
                    img_weather_image.setImageResource(R.drawable.forest_cloudy)
                    lyt_values.setBackgroundColor(resources.getColor(R.color.cloudy))
                }
                "Rain" -> {
                    temperature.text =
                        Utility.convertToString(weatherAttribute.mainAttributes?.temp!!) + "??" + "\n RAINY"
                    img_weather_image.setImageResource(R.drawable.forest_rainy)
                    lyt_values.setBackgroundColor(resources.getColor(R.color.rainy))
                }
                "Clear" -> {
                    temperature.text =
                        Utility.convertToString(weatherAttribute.mainAttributes?.temp!!) + "??" + "\n SUNNY"
                    img_weather_image.setImageResource(R.drawable.forest_sunny)
                    lyt_values.setBackgroundColor(resources.getColor(R.color.sunny))
                }
                else -> {
                    temperature.text =
                        Utility.convertToString(weatherAttribute.mainAttributes?.temp!!) + "??" + "\n" + (weatherList!![0].main)
                    img_weather_image.setImageResource(R.drawable.forest_sunny)
                    lyt_values.setBackgroundColor(resources.getColor(R.color.sunny))
                }
            }
        }
    }

    private fun renderForecastList(weatherAttributes: ArrayList<WeatherAttribute>) {
        adapter.addData(weatherAttributes)
        adapter.notifyDataSetChanged()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return when (item.itemId) {
            R.id.action_favourite -> {
                item.setIcon(R.drawable.ic_like)
                GlobalScope.launch {
                    saveFavouriteLocation()
                }
                showSuccessToast(context, getString(R.string.success_saved))
                true
            }
            R.id.action_view_favourites -> {
                var intent = Intent(this, FavouritesLocationsList::class.java)
                startActivity(intent)
                true
            }
            R.id.action_more_about_location -> {
                if (Utility.isNetWorkAvailable(context)) {
                    showExtraLocationDetails()
                }else{
                    Utility.showErrorToast(context,getString(R.string.error))
                }

                true
            }
            R.id.action_map_favourites -> {
                var intent = Intent(this, FavouritesLocationsOnMap::class.java)
                startActivity(intent)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onStarted() {
        Utility.showProgressBar(this, this@MainActivity)
        when {
            isGetCurrentWeather -> {
                weatherViewModel.returnCurrentWeatherData().observe(this, {

                })
            }
            isGetLocalData -> weatherViewModel.returnWeatherData()?.observe(this, {

            })
            else -> {
                weatherViewModel.returnWeatherForecastData().observe(this, {

                })
            }
        }

    }

    override fun onSuccess(response: Any?) {
        when {
            isGetCurrentWeather -> {
                var responseData = response as MutableLiveData<WeatherAttribute>
                responseData?.observe(this, { it ->
                    Utility.hideProgressBar(this)
                    isGetCurrentWeather = false
                    if (it.hasError!!) {
                        this.onFailure(getString(R.string.get_weather_error))
                    } else {
                        if (it.weatherList.isNullOrEmpty()) {
                            this.onFailure(getString(R.string.get_weather_error))
                        } else {
                            weatherInfo = it as WeatherAttribute
                            GlobalScope.launch {
                                saveCurrentWeatherDetails()
                            }
                            displayCurrentWeather(it)
                            //get the weather forecast
                            getWeatherForecastDetails(latitude, longitude)
                        }
                    }
                })
            }
            isGetLocalData -> {
                var res = response as LiveData<List<Weather>>
                res?.observe(this, {
                    Utility.hideProgressBar(this)
                    isGetLocalData = false
                    if (it.isNotEmpty()) {
                        Utility.showCustomErrorDialog(
                            context,
                            getString(R.string.offline),
                            getString(R.string.warning)
                        )
                        displayLocallyFetchedDetails(it[0])
                    }
                })

            }
            else -> {
                var responseData = response as MutableLiveData<WeatherForecastList>
                responseData?.observe(this, { it ->
                    Utility.hideProgressBar(this)
                    if (it.hasError!!) {
                        this.onFailure(getString(R.string.get_weather_error))
                    } else {
                        if (it.weatherForecastList.isNullOrEmpty()) {
                            this.onFailure(getString(R.string.get_weather_error))
                        } else {
                            val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val timeFormat = SimpleDateFormat("HH:mm:ss")

                            //Weather details available. Take the weather forecast each day at 09.00am.
                            var filteredList = it.weatherForecastList!!.filter { item ->
                                var formattedDate: Date = dateFormat.parse(item.dateTime)
                                timeFormat.format(formattedDate) == "09:00:00"
                            }
                            if (filteredList.isNotEmpty()) {
                                renderForecastList(filteredList as ArrayList<WeatherAttribute>)
                            }


                        }
                    }
                })
            }
        }
    }

    override fun onFailure(message: String?) {
        Utility.hideProgressBar(this)
        Utility.showCustomErrorDialog(context, message, getString(R.string.failed))
    }

    private fun grantPermissionsDialog() {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_warning)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById(R.id.bt_close) as AppCompatButton).setOnClickListener {
            dialog.dismiss()
            requestPermissions()
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showCustomErrorDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_error)
        dialog.setCancelable(true)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        (dialog.findViewById<View>(R.id.title) as TextView).text = getString(R.string.error)
        (dialog.findViewById<View>(R.id.content) as TextView).text =
            getString(R.string.switch_on_location)
        (dialog.findViewById<View>(R.id.bt_close) as AppCompatButton).setOnClickListener {
            dialog.dismiss()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
        dialog.show()
        dialog.window!!.attributes = lp
    }

    private fun showExtraLocationDetails() {
        var mBehavior = BottomSheetBehavior.from(bottom_sheet)
        if (mBehavior.state === BottomSheetBehavior.STATE_EXPANDED) {
            mBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
        val view: View = layoutInflater.inflate(R.layout.location_details, null)
        mBottomSheetDialog = BottomSheetDialog(this)
        mBottomSheetDialog?.setContentView(view)
        (view.findViewById<View>(R.id.tv_country_name) as TextView).text = countryName
        (view.findViewById<View>(R.id.tv_location_name) as TextView).text = weatherInfo.regionName!!
        (view.findViewById<View>(R.id.tv_admin_area) as TextView).text = adminArea
        (view.findViewById<View>(R.id.tv_country_code) as TextView).text = countryCode
        (view.findViewById<View>(R.id.tv_latitude) as TextView).text = latitude.toString()
        (view.findViewById<View>(R.id.tv_longitude) as TextView).text = longitude.toString()
        (view.findViewById<View>(R.id.bt_submit) as Button).setOnClickListener {
            mBottomSheetDialog?.dismiss()
        }
        (view.findViewById<View>(R.id.bt_close) as ImageButton).setOnClickListener {
            mBottomSheetDialog?.dismiss()
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBottomSheetDialog!!.window
                ?.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        }
        (view.parent as View).setBackgroundColor(resources.getColor(android.R.color.transparent))
        mBottomSheetDialog?.show()
        mBottomSheetDialog?.setOnDismissListener {
            mBottomSheetDialog = null
        }
    }


}