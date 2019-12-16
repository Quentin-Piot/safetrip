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


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

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



    override fun onBind(intent: Intent?): IBinder? {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}
