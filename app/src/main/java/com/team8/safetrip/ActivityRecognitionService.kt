package com.team8.safetrip

import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder

import android.widget.Toast
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.DetectedActivity
import java.util.*

class ActivityRecognitionService : Service() {
    lateinit var context: Context
    companion object {


        var INSTANCE: ActivityRecognitionService? = null

    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {

        INSTANCE = this
        context = this


        val intent = Intent(this, TransitionBroadcastReceiver::class.java)
        val pendingIntentBroadcast =
            PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val transitions: List<ActivityTransition> = getTransitionActivityList()
        val request = ActivityTransitionRequest(transitions)

        startGetBroadcast(pendingIntentBroadcast, request, "pendingIntentBroadcast")

        refresh()
        if(!MainActivity.launchedAll) Toast.makeText(this,"Activity Recognition service launched", Toast.LENGTH_SHORT).show()

        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null

    }

    private fun refresh() {

    }

    private fun startGetBroadcast(
        pendingIntent: PendingIntent,
        request: ActivityTransitionRequest,
        type: String
    ) { // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val task =
            ActivityRecognition.getClient(this)
                .requestActivityTransitionUpdates(request, pendingIntent)
        task.addOnSuccessListener {
//            Toast.makeText(context, "Waiting for Activity Transitions...", Toast.LENGTH_LONG)
//                .show()
        }
        task.addOnCompleteListener {
            //Toast.makeText(context, "oncomplete " + type, Toast.LENGTH_SHORT).show();
        }
        task.addOnFailureListener { e ->
            Toast.makeText(context, "Error : $e", Toast.LENGTH_LONG).show()
        }
    }

    var detectedActivity = intArrayOf(
        DetectedActivity.IN_VEHICLE,
        DetectedActivity.ON_BICYCLE,
        DetectedActivity.RUNNING,
        DetectedActivity.STILL,
        DetectedActivity.WALKING
    )

    private fun getTransitionActivityList(): List<ActivityTransition> {
        val transitions: MutableList<ActivityTransition> =
            ArrayList()
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.ON_BICYCLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )

        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()
        )
        transitions.add(
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.RUNNING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        )
        return transitions
    }


}