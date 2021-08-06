package com.test.weatherapp.ui.main.view

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProviders
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.test.weatherapp.R
import com.test.weatherapp.data.room.entities.Favourites
import com.test.weatherapp.ui.main.listener.UniversalListener
import com.test.weatherapp.ui.main.viewmodel.FavouritesViewModel
import com.test.weatherapp.ui.viewmodelfactories.FavouritesViewModelFactory
import com.test.weatherapp.utils.Utility


class FavouritesLocationsOnMap : AppCompatActivity(), OnMapReadyCallback,
    ConnectionCallbacks,
    OnConnectionFailedListener, UniversalListener {
    private var context = this
    private lateinit var favouritesViewModel: FavouritesViewModel
    private lateinit var mLocationRequest: LocationRequest
    val MY_PERMISSIONS_REQUEST_LOCATION = 99
    private lateinit var mGoogleMap: GoogleMap
    private lateinit var mGoogleApiClient: GoogleApiClient
    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private lateinit var mLastLocation: Location
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favourite_map)
        setSupportActionBar(findViewById(R.id.toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        initResources()

    }

    private fun initResources(){
        favouritesViewModel =
            ViewModelProviders.of(this, FavouritesViewModelFactory(this))
                .get(FavouritesViewModel::class.java)
        favouritesViewModel.listener = this
    }

    private fun getAllFavourites() {
        favouritesViewModel.getFavourites()
    }
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        mGoogleMap!!.mapType = GoogleMap.MAP_TYPE_NORMAL
        getAllFavourites()
        checkLocationPermissions()
    }

    private fun checkLocationPermissions(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
                === PackageManager.PERMISSION_GRANTED
            ) {
                buildGoogleApiClient()
                mGoogleMap!!.isMyLocationEnabled = true
            } else {
                requestLocationPermissions()
            }
        } else {
            buildGoogleApiClient()
            mGoogleMap!!.isMyLocationEnabled = true
        }

    }

    @Synchronized
    fun buildGoogleApiClient() {
        mGoogleApiClient = GoogleApiClient.Builder(this)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build()
        mGoogleApiClient.connect()
    }

    override fun onConnected(p0: Bundle?) {

        mLocationRequest = LocationRequest()
        mLocationRequest.interval = 120
        mLocationRequest.fastestInterval = 120
        mLocationRequest.priority = LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
            === PackageManager.PERMISSION_GRANTED
        ) {
            mFusedLocationClient.requestLocationUpdates(
                mLocationRequest,
                mLocationCallback,
                Looper.myLooper()
            )
        }
    }


    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            for (location in locationResult.locations) {
                mLastLocation = location
                val latLng = LatLng(location.latitude, location.longitude)
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13f))
            }
        }
    }

    override fun onConnectionSuspended(p0: Int) {

    }

    override fun onConnectionFailed(p0: ConnectionResult) {

    }


    private fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            !== PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            ) {
                AlertDialog.Builder(this)
                    .setTitle(getString(R.string.location_needed))
                    .setMessage(getString(R.string.switch_on_location))
                    .setPositiveButton(
                        getString(R.string.ok)
                    ) { dialogInterface, i ->
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                            MY_PERMISSIONS_REQUEST_LOCATION
                        )
                    }
                    .create()
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_LOCATION
                )
            }
        }
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
                displayMarkersOnMap(it)
            } else {
                this.onFailure(getString(R.string.no_favourites))
            }
        })
    }

    override fun onFailure(message: String?) {
        Utility.hideProgressBar(this)
        Utility.showCustomErrorDialog(context, message, getString(R.string.failed))
    }

    private fun displayMarkersOnMap(favourites: List<Favourites>){
        for (item in favourites){
            val marker = mGoogleMap.addMarker(
                MarkerOptions().position(
                    LatLng(
                        item.latitude,
                        item.longitude
                    )
                ).icon(BitmapDescriptorFactory.fromResource(R.drawable.placeholder)).draggable(true)
                    .title(item.name).snippet(item.countryName)
            )
            marker.showInfoWindow()
        }

    }
}