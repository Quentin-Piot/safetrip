package com.team8.safetrip

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.activity_agression.*


class AgressionActivity : AppCompatActivity(), OnMapReadyCallback {

    private var mapView: MapView? = null
    private var gmap: GoogleMap? = null

    private var latitude = 0.0
    private var longitude = 0.0
    private var distance = 0.0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agression)

        latitude = MyFirebaseMessagingService.latitude
        longitude = MyFirebaseMessagingService.longitude
        distance = MyFirebaseMessagingService.distance

        locA.text = MyFirebaseMessagingService.location
        distText.text = "Distance : ${distance} km"
        latText.text = "Latitude : ${latitude}°"
        longText.text = "Longitude : ${longitude}°"




        var mapViewBundle: Bundle? = null


        mapView = findViewById(R.id.map_view)
        mapView!!.onCreate(mapViewBundle)
        mapView!!.getMapAsync(this)

    }



    override fun onResume() {
        super.onResume()
        mapView!!.onResume()
    }

    override fun onStart() {
        super.onStart()
        mapView!!.onStart()
    }

    override fun onStop() {
        super.onStop()
        mapView!!.onStop()
    }

    override fun onPause() {
        mapView!!.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapView!!.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView!!.onLowMemory()
    }

    override fun onMapReady(googleMap: GoogleMap) {
        gmap = googleMap
        gmap!!.setMinZoomPreference(14f)
        val uiSettings: UiSettings = gmap!!.getUiSettings()
        uiSettings.isIndoorLevelPickerEnabled = true
        uiSettings.isMyLocationButtonEnabled = true
        uiSettings.isMapToolbarEnabled = true
        uiSettings.isCompassEnabled = true
        uiSettings.isZoomControlsEnabled = true
        val pos = LatLng(latitude, longitude)

        val markerOptions = MarkerOptions()
        markerOptions.position(pos)
        gmap!!.addMarker(markerOptions)
        gmap!!.moveCamera(CameraUpdateFactory.newLatLng(pos))
    }


}