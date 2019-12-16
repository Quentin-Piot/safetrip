package com.team8.safetrip


import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.*
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
    private lateinit var activityRecognition: Intent


    companion object {


        var launchedAll = false
        var activityLaunched = false
        var detectionServiceLaunched = false

        var serviceLocalisationLaunched = false
        var serviceFallLaunched = false
        var activityRecognitionLaunched = false


        var debugNoVolume = false




    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityLaunched = true
        setContentView(R.layout.activity_main)
        setupPermissions()
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !notificationManager.isNotificationPolicyAccessGranted) {
            val alertDialog = AlertDialog.Builder(this).create()
            alertDialog.setTitle("Allow access to silent mode")
            alertDialog.setMessage("In order to be able to hear the notification when someone nearby is attacked, even if your phone is on silent mode, you need to give the application access to your silent mode")
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK"
            ) { dialog, _ -> dialog.dismiss()
                val intent = Intent(
                    android.provider.Settings
                        .ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS)

                startActivity(intent)
            }
            alertDialog.show()

        }
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/alert")
        Thread.setDefaultUncaughtExceptionHandler(onRuntimeError)



        LocalBroadcastManager.getInstance(this).registerReceiver(
            mMessageReceiver,  IntentFilter("intentKey"))



        Locbutton.setOnClickListener {

            if(LocalisationService.INSTANCE == null){
                serviceLocalisation = Intent(this, LocalisationService::class.java)
                startService(serviceLocalisation)
                serviceLocalisationLaunched = true

                Locbutton.text = "Stop Localisation"
            }else{
                stopService(serviceLocalisation)
                serviceLocalisationLaunched = false
                Locbutton.text = "Launch Localisation"



            }
        }


        Fallbutton.setOnClickListener {
            if(FallService.INSTANCE == null){
                serviceFall = Intent(this, FallService::class.java)
                serviceStarted()
                serviceFallLaunched = true
                startService(serviceFall)
                Fallbutton.text = "Stop Fall Detection"


            }else{
                serviceFallLaunched = false
                Fallbutton.text = "Launch Fall Detection"
                stopService(serviceFall)


                serviceStarted()

            }
        }




        ARbutton.setOnClickListener {
            if(ActivityRecognitionService.INSTANCE == null){
                activityRecognition = Intent(this, ActivityRecognitionService::class.java)
                startService(activityRecognition)
                serviceStarted()
                activityRecognitionLaunched = true

                ARbutton.text = "Stop Activity Recognition"


            }else{
                stopService(activityRecognition)
                activityRecognitionLaunched = false
                ARbutton.text = "Launch Activity Recognition"
                serviceStarted()

            }
        }



        Allbutton.setOnClickListener {
            launchedAll = true
            serviceLocalisation = Intent(this, LocalisationService::class.java)
            startService(serviceLocalisation)
            serviceFall = Intent(this, FallService::class.java)
            startService(serviceFall)
            activityRecognition = Intent(this, ActivityRecognitionService::class.java)
            startService(activityRecognition)
            Toast.makeText(this, "All services launched", Toast.LENGTH_LONG).show()


            Locbutton.text = "Stop Localisation"
            Fallbutton.text = "Stop Fall Detection"
            ARbutton.text = "Stop Activity Recognition"
            activityRecognitionLaunched = true
            serviceFallLaunched = true
            serviceLocalisationLaunched = true
            serviceStarted()
        }


        checkBoxVolume.setOnClickListener {
            debugNoVolume = checkBoxVolume.isChecked
        }



        val messagingService = Intent(this, MyFirebaseMessagingService::class.java)
        startService(messagingService)




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
        stopService(activityRecognition)
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
        if(!AlertActivity.created) {
            val intent = Intent(this, AlertActivity::class.java)


            startActivity(intent)
        }
    }


    private val mMessageReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(
            context: Context,
            intent: Intent
        ) { // Get extra data included in the Intent
            when (intent.getStringExtra("key")) {
                "UpdateLocation" -> locationT.text = LocalisationService.location
                "UpdateLogs" -> {
                    logs.text = TransitionBroadcastReceiver.logs
                    currentAct.text = "Current activity : ${TransitionBroadcastReceiver.currentActivity}"
                }
                "Alarm" -> {
                    launchAlarm()
                }
            }
        }
    }
    private val onRuntimeError = Thread.UncaughtExceptionHandler { _, _ ->
        val intentSettings = Intent(this, MainActivity::class.java)
        startActivityForResult(intentSettings, 0)
    }


    private fun serviceStarted(){

        if(!detectionServiceLaunched){
            detectionServiceLaunched = true

            showNotification()

//
//            val backgroundService = Intent(this, BackgroundService::class.java)
//            startService(backgroundService)



        }else if(!serviceFallLaunched && !activityRecognitionLaunched){
                detectionServiceLaunched = false
                Toast.makeText(this,"No detection service is running anymore",Toast.LENGTH_SHORT).show()
                val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                mNotificationManager.cancel(1)

        }

    }


    private fun showNotification() {
        val mNotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("2",
                "background running",
                NotificationManager.IMPORTANCE_DEFAULT)
            channel.description = "it's the notifications that say background"
            mNotificationManager.createNotificationChannel(channel)
        }
        val mBuilder = NotificationCompat.Builder(applicationContext, "2")
            .setSmallIcon(R.drawable.ic_notification_icon) // notification icon
            .setContentTitle("Safe Trip is activated") // title for notification
            .setContentText("Safe Trip is running in background, you're safe to go home")// message for notification
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("Safe Trip is running in background, you're safe to go home"))

            // clear notification after click
            .setAutoCancel(false)
            .setOngoing(true);
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
        mBuilder.setContentIntent(pi)
        mNotificationManager.notify(1, mBuilder.build())
    }






}






