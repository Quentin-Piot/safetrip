package com.team8.safetrip


import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_main.*
import android.widget.TextView
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import java.util.*


class MainActivity : AppCompatActivity(){

    private lateinit var serviceLocalisation: Intent
    private lateinit var serviceFall: Intent
    private lateinit var serviceShake: Intent
    private lateinit var activityRecognition: Intent
    private lateinit var battery: Intent


    companion object {


        var launchedAll = false
        var activityLaunched = false


    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLaunched = true
        setContentView(R.layout.activity_main)
        setupPermissions()
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/alert")
        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError)


        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,  IntentFilter("intentKey"))


        Shakebutton.setOnClickListener {

            if(ShakeService.INSTANCE == null) {
                serviceShake = Intent(this, ShakeService::class.java)
                startService(serviceShake)
                }
        }

        Locbutton.setOnClickListener {

            if(LocalisationService.INSTANCE == null){
                serviceLocalisation = Intent(this, LocalisationService::class.java)
                startService(serviceLocalisation)
            }
        }


        Fallbutton.setOnClickListener {
            if(FallService.INSTANCE == null){
                serviceFall = Intent(this, FallService::class.java)
                startService(serviceFall)
            }
        }




        ARbutton.setOnClickListener {
            if(ActivityRecognitionService.INSTANCE == null){
                activityRecognition = Intent(this, ActivityRecognitionService::class.java)
                startService(activityRecognition)
            }
        }

        Batbutton.setOnClickListener {

            if(BatteryService.INSTANCE == null){
                battery = Intent(this, BatteryService::class.java)
                startService(battery)
            }
        }

        Allbutton.setOnClickListener {
            launchedAll = true
            serviceShake = Intent(this, ShakeService::class.java)
            startService(serviceShake)
            serviceLocalisation = Intent(this, LocalisationService::class.java)
            startService(serviceLocalisation)
            serviceFall = Intent(this, FallService::class.java)
            startService(serviceFall)
            activityRecognition = Intent(this, ActivityRecognitionService::class.java)
            startService(activityRecognition)
            battery = Intent(this, BatteryService::class.java)
            startService(battery)
            Toast.makeText(this, "All services launched", Toast.LENGTH_LONG).show()

        }





        val messagingService = Intent(this, MyFirebaseMessagingService::class.java)
        startService(messagingService)


        val backgroundService = Intent(this, BackgroundService::class.java)
        startService(backgroundService)

        settingsButton.setOnClickListener {

            val intentSettings = Intent(this, Settings::class.java)
            startActivityForResult(intentSettings, 0)


        }


    }




    override  fun onStop(){
        super.onStop()
        activityLaunched = false
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceLocalisation)
        stopService(serviceFall)
        stopService(serviceShake)
        stopService(activityRecognition)
        stopService(battery)
    }



    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permission3 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )


        val permission4 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )



        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED || permission4 != PackageManager.PERMISSION_DENIED) {
            //  Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            ),
            1
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED || grantResults[4] != PackageManager.PERMISSION_GRANTED || grantResults[5] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Please accept all permissions", Toast.LENGTH_SHORT).show()

                    setupPermissions()
                } else {

                    //Toast.makeText(applicationContext, "All permissions granted", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }




    private fun launchAlarm(){
        val intent = Intent(this, AlertActivity::class.java)


        startActivity(intent)
    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { // Get extra data included in the Intent
            val message = intent.getStringExtra("key")
            if (message == "UpdateLocation") locationT.text = LocalisationService.location
            else if (message == "UpdateLogs") {
                logs.text = TransitionBroadcastReceiver.logs
                currentAct.text = "Current activity : ${TransitionBroadcastReceiver.currentActivity}"
            }else if(message == "Alarm"){
               launchAlarm()
            }
        }
    }
    private val onRuntimeError = Thread.UncaughtExceptionHandler { thread, ex ->
        val intentSettings = Intent(this, MainActivity::class.java)
        startActivityForResult(intentSettings, 0)
    }






}






