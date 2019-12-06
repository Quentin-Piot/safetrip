package com.team8.safetrip

import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.*
import java.util.*


class LocalisationService  : Service() {

    lateinit var mFusedLocationClient: FusedLocationProviderClient

    lateinit var geocoder : Geocoder

    companion object {

        var latitude : Double = 36.3684239
        var longitude : Double = 127.3569268
        var location : String = ""

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        geocoder = Geocoder(this, Locale.getDefault())

        val t = Timer()

        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {

                    requestNewLocationData()







                }
            },  //Set how long before to start calling the TimerTask (in milliseconds)
            0,  //Set the amount of time between each execution (in milliseconds)
            10000
        )


        return Service.START_STICKY
    }






    private fun getLastLocation(){

        if (isLocationEnabled) {
            mFusedLocationClient.lastLocation.addOnCompleteListener { task ->
                val location = task.result
                if (location == null) {
                    requestNewLocationData()
                } else {
                    latitude = location.latitude
                    longitude = location.longitude
                }
            }
        } else {
            Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val mLocationRequest = LocationRequest()
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = 0
        mLocationRequest.fastestInterval = 0
        mLocationRequest.numUpdates = 1
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mFusedLocationClient.requestLocationUpdates(
            mLocationRequest, mLocationCallback,
            Looper.getMainLooper()
        )
    }

    private val mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val mLastLocation = locationResult.lastLocation
            latitude = mLastLocation.latitude
            longitude = mLastLocation.longitude
            val addresses: List<Address> = geocoder.getFromLocation(
                latitude,
                longitude
                ,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            val address: String = addresses[0]
                .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            location = "Location : $address"
            sendMessageToActivity("UpdateLocation")
        }
    }

    private val isLocationEnabled: Boolean
             get() {
            val locationManager =
                getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }
    private fun sendMessageToActivity(msg: String) {
        val intent = Intent("intentKey")
        // You can also include some extra data.
        intent.putExtra("key", msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent)
    }


    }

