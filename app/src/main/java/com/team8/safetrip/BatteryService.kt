package com.team8.safetrip

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.IBinder
import java.util.*
import android.R
import android.widget.TextView
import android.os.Bundle
import android.content.BroadcastReceiver
import android.content.Context
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.util.Log
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.R.attr.name
import android.R.attr.start
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager


class BatteryService : Service() {

    private var check = false
    private var prevLevel = 100
    private lateinit var mBatInfoReceiver: BroadcastReceiver
    private var startTime = 0L

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTime = System.currentTimeMillis()
        Log.d("LOCKSCREEN", "battery started")

        mBatInfoReceiver = object : BroadcastReceiver() {
            override fun onReceive(ctxt: Context, intent: Intent) {
                val level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0)
                if (check) {
                    //Log.d("BATTERY_LEVEL", "battery has dropped by ${prevLevel - level}% in the last minute")
                    Toast.makeText(this@BatteryService, "The battery level has dropped by ${prevLevel - level}% in the last minute", Toast.LENGTH_LONG).show()
                    check = false
                    prevLevel = level
                }
            }
        }
        this.registerReceiver(this.mBatInfoReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        val t = Timer()
        t.scheduleAtFixedRate(
            object : TimerTask() {
                override fun run() {
                    check = true
                }
            },  //Set how long before to start calling the TimerTask (in milliseconds)
            0,  //Set the amount of time between each execution (in milliseconds)
            60000
        )
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("LOCKSCREEN", "battery destroyed after ${System.currentTimeMillis()- startTime}")
        unregisterReceiver(mBatInfoReceiver)
    }

}
