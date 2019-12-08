package com.team8.safetrip

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Handler
import android.os.IBinder
import android.os.SystemClock
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class ShakeService : Service(), SensorEventListener {

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mAccel: Float = 0.toFloat() // acceleration apart from gravity
    private var mAccelCurrent: Float = 0.toFloat() // current acceleration including gravity
    private var mAccelLast: Float = 0.toFloat() // last acceleration including gravity


    private var count = 0
    private var startMillis: Long = 0





    companion object {
        var alarmActivated = false

        var INSTANCE: ShakeService? = null



    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        INSTANCE = this
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mSensorManager!!.registerListener(this, mAccelerometer,
            SensorManager.SENSOR_DELAY_UI, Handler())

        if(!MainActivity.launchedAll) Toast.makeText(this,"Shake service launched", Toast.LENGTH_SHORT).show()
        return Service.START_STICKY
    }



    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null
        mSensorManager?.unregisterListener(this)
    }



    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

    override fun onSensorChanged(event: SensorEvent) {
        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        mAccelLast = mAccelCurrent
        mAccelCurrent = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = mAccelCurrent - mAccelLast
        mAccel = mAccel * 0.9f + delta // perform low-cut filter
        val time = System.currentTimeMillis()

        if(time -startMillis >= 900){
            count = 0
            startMillis = time

        }
        //if it is the first time, or if it has been more than 3 seconds since the first tap ( so it is like a new try), we reset everything


        if (mAccel > 7 && !alarmActivated) {


            if (startMillis == 0L || (time - startMillis > 250 && time -startMillis < 900)) {
                startMillis = time
                count++
                println("count $count")

                if (count >= 3) {

                    ring()




                }
            }




        }
    }


    private fun sendMessageToActivity(msg: String) {


        val i = Intent("intentKey")
        i.putExtra("key", msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }




    private fun ring(){
        if(!AlertActivity.created) {
            alarmActivated = true

            sendMessageToActivity("Alarm")

        }

    }








}
