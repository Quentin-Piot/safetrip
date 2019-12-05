package com.example.falldetection


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {

    private var mp: MediaPlayer? = null

    private lateinit var receiver: BroadcastReceiver

    private var runService = true
    private var collectingData = true


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACTIVITY_RECOGNITION), 0)

        mp = MediaPlayer.create(this, R.raw.y)
        mp?.setVolume(0.5f, 0.5f)

        val myIntent = Intent(this, FallService::class.java)

        other.setOnClickListener {
            fall.text = "All good in the neighbourhood"

            toggleButtons(collectingData)
            toggleService(runService, myIntent)
        }

        val filter = IntentFilter()
        filter.addAction("testing_action")
        receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Log.d("broadcast", intent?.action.toString())
                if (intent?.action == "testing_action") {
                    fall.text = "OH NO! A fall has been detected!"
                    mp?.start()
                }
                toggleButtons(collectingData)
                toggleService(runService, myIntent)
            }
        }
        registerReceiver(receiver, filter)
    }

    private fun toggleButtons(bool: Boolean) {
        if (bool) {
            other.text = "Stop"
        }
        else {
            other.text = "Start"
        }
        collectingData = !collectingData
    }

    private fun toggleService(bool: Boolean, myIntent: Intent){
        if (bool) {
            startService(myIntent)
            Log.d("Toggle", "ON")
        }
        else {
            stopService(myIntent)
            Log.d("Toggle", "OFF")
        }
        runService = !runService
    }

    override fun onDestroy() {
        super.onDestroy()
        mp?.release()
        unregisterReceiver(receiver)
    }
}
