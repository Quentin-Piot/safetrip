package com.team8.safetrip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityTransitionResult

class TransitionBroadcastReceiver : BroadcastReceiver() {
    companion object {
        var logs : String =  ""
        var currentActivity : String = "STILL"
    }
    private lateinit var context : Context
    override fun onReceive(context: Context, intent: Intent) {
        this.context = context
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)
            if (result != null) {
                for (event in result.transitionEvents) {
                    saveActivity(ActivityTransitionEventWrapper(event))
                }
            }
        }
    }

    private fun saveActivity(event: ActivityTransitionEventWrapper) {
        logs += "\n ${event.eventDisplayFormat}"
        currentActivity = event.activityName
        sendMessageToActivity("UpdateLogs")
    }

    private fun sendMessageToActivity(msg: String) {

        val i = Intent("intentKey")
        i.putExtra("key", msg)
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(i)
    }
    }