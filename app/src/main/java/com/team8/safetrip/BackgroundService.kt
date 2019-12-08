package com.team8.safetrip

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.widget.Toast
import androidx.core.app.NotificationCompat
import weka.gui.Main


class BackgroundService : Service() {

    companion object{
        var localisationServiceLaunched = false
        var fallServiceLaunched = false
        var shakeServiceLaunched = false
        var batteryServiceLaunched = false
        var activityRecognitionServiceLaunched = false
    }
    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        if(!MainActivity.activityLaunched) {
            val dialogIntent = Intent(this, MainActivity::class.java)
            dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(dialogIntent)
            showNotification()

        }

        return START_STICKY
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)


        Toast.makeText(this, "SafeTrip has been closed, it will be launched in few seconds", Toast.LENGTH_LONG).show()

        val service = PendingIntent.getService(
            applicationContext,
            1001,
            Intent(applicationContext, BackgroundService::class.java),
            PendingIntent.FLAG_ONE_SHOT
        )

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        alarmManager[AlarmManager.ELAPSED_REALTIME_WAKEUP, 200] = service
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

    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
