package com.team8.safetrip

import android.app.*

import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.IBinder
import android.os.Looper
import android.provider.Settings
import android.widget.Toast
import com.google.android.gms.location.*
import java.util.*


class LocalisationService  : Service() {

    private lateinit var mFusedLocationClient: FusedLocationProviderClient
    private  var longitude : Double = 0.0
    private  var latitude : Double = 0.0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val t = Timer()

        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {

                    getLastLocation()
                    println("Latitute : $latitude")
                    println("Longitude : $longitude")

                }
            },  //Set how long before to start calling the TimerTask (in milliseconds)
            0,  //Set the amount of time between each execution (in milliseconds)
            10000
        )


        return Service.START_STICKY
    }



    private fun getLastLocation() {
                if (isLocationEnabled()) {

                    mFusedLocationClient!!.lastLocation.addOnCompleteListener  { task ->
                        var location: Location? = task.result
                        if (location == null) {
                            requestNewLocationData()
                        } else {
                            latitude= location.latitude
                            longitude = location.longitude
                        }
                    }
                } else {
                    Toast.makeText(this, "Turn on location", Toast.LENGTH_LONG).show()
                    val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    startActivity(intent)
                }

        }


        private fun requestNewLocationData() {
            var mLocationRequest = LocationRequest()
            mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            mLocationRequest.interval = 0
            mLocationRequest.fastestInterval = 0
            mLocationRequest.numUpdates = 1

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
            mFusedLocationClient!!.requestLocationUpdates(
                mLocationRequest, mLocationCallback,
                Looper.myLooper()
            )
        }

        private val mLocationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                var mLastLocation: Location = locationResult.lastLocation
                latitude = mLastLocation.latitude
                longitude = mLastLocation.longitude
            }
        }

        private fun isLocationEnabled(): Boolean {
            var locationManager: LocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
                LocationManager.NETWORK_PROVIDER
            )
        }

    }

