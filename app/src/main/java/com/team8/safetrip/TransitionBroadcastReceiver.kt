package com.team8.safetrip

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.CountDownTimer
import android.text.BoringLayout
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.location.ActivityTransitionResult
import kotlinx.android.synthetic.main.activity_alert.*
import java.lang.Exception

class TransitionBroadcastReceiver : BroadcastReceiver() {
    companion object {
        var logs : String =  ""
        var currentActivity : String = "STILL"
    }
    private var lastActivity : String = "STILL"

    private lateinit var timer : CountDownTimer

    private lateinit var context : Context

    private var timerLaunched : Boolean = false
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
        lastActivity = currentActivity
        currentActivity = event.activityName
        checkIfProblem()
        sendMessageToActivity("UpdateLogs")
    }

    private fun sendMessageToActivity(msg: String) {

        val i = Intent("intentKey")
        i.putExtra("key", msg)
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(i)
    }



    private fun checkIfProblem(){

        if(currentActivity == "RUNNING" || currentActivity == "IN_VEHICLE"){
                   activeAlert()
        }else if(currentActivity == "STILL" && lastActivity != "STILL" && !timerLaunched ){
            timerLaunched = true
            Toast.makeText(context, "Timer launched", Toast.LENGTH_SHORT).show()

            timer = object: CountDownTimer(10000, 0) {

                override fun onTick(millisUntilFinished: Long) {
                    println("tick")
                }

                override fun onFinish() {
                    println("cancel")
                    timerLaunched = false
                    activeAlert()
                }
            }.start()

        }else if(lastActivity == "STILL" && currentActivity != "STILL"){
            try {
                timer.cancel()
                timerLaunched = false
            }catch (e : Exception){

            }
        }


    }


    private fun activeAlert(){
        if(!AlertActivity.created) {
            Toast.makeText(context, "Alarm Launched", Toast.LENGTH_SHORT).show()
            sendMessageToActivity("Alarm")



        }

    }
    }