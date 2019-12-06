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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationCompat.VISIBILITY_PUBLIC
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity(){

    private lateinit var serviceLocalisation: Intent
    private lateinit var serviceFall: Intent
    private lateinit var serviceShake: Intent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        setupPermissions()
        showNotification()


        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,  IntentFilter("intentKey"));

        serviceLocalisation = Intent(this, LocalisationService::class.java)
        startService(serviceLocalisation)

        serviceFall = Intent(this, FallService::class.java)
        startService(serviceFall)


        serviceShake = Intent(this, ShakeService::class.java)
        startService(serviceShake)

        val activityRecognition = Intent(this, ActivityRecognitionService::class.java)
        startService(activityRecognition)




        settingsButton.setOnClickListener {

            val intentSettings = Intent(this, Settings::class.java)
            startActivityForResult(intentSettings, 0)


        }


    }

    override fun onPause() {
        super.onPause()
        stopService(serviceLocalisation)
        stopService(serviceFall)
        stopService(serviceShake)
    }

    override fun onResume() {
        super.onResume()
        startService(serviceLocalisation)
        startService(serviceFall)
        startService(serviceShake)
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(serviceLocalisation)
        stopService(serviceFall)
        stopService(serviceShake)
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

                    Toast.makeText(applicationContext, "All permissions granted", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


    private fun showNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("YOUR_CHANNEL_ID",
                "YOUR_CHANNEL_NAME",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "YOUR_NOTIFICATION_CHANNEL_DISCRIPTION"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "YOUR_CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_notification_icon) // notification icon
            .setContentTitle("Safe Trip is activated") // title for notification
            .setContentText("Safe Trip is running in background, you're safe to go home")// message for notification
            .setVisibility(VISIBILITY_PUBLIC)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Safe Trip is running in background, you're safe to go home"))

            // clear notification after click
            .setAutoCancel(false)
            .setOngoing(true);
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(0, mBuilder.build())
    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { // Get extra data included in the Intent
            val message = intent.getStringExtra("key")
            if(message == "UpdateLocation"){

                locationT.text = LocalisationService.location.toString()
            }else if(message == "UpdateLogs"){
                logs.text = TransitionBroadcastReceiver.logs
            }
        }
    }






}






