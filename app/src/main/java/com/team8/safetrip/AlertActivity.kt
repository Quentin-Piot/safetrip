package com.team8.safetrip

import android.app.NotificationManager
import android.content.Context
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.telephony.SmsManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.android.synthetic.main.activity_alert.*
import org.json.JSONException
import org.json.JSONObject


class AlertActivity : AppCompatActivity() {

    private lateinit var mAudioManager : AudioManager
    private lateinit var mp : MediaPlayer
    private lateinit var data : Data
    private lateinit var pass : String

    private val FCM_API = "https://fcm.googleapis.com/fcm/send"
    private val serverKey =
        "key=" + "AAAAJaVNcog:APA91bEhg0SJTAYDJ7YzyvnDudMOJThDZm_JAFwmiVBqB5adTS_FZf8f6O8bMyMHBa28cokYXQNQxYYi1E_J4XWsWhSuqRbV0wRxqXdoi99PE0thrGXAkFH3P2geHl5gTktZrvVcM14F"
    private val contentType = "application/json"

    private var dataObj = Data()
    private var listNumber: ArrayList<String> = arrayListOf()

    private  var timerStopped : Boolean = false

    private val requestQueue: RequestQueue by lazy {
        Volley.newRequestQueue(this.applicationContext)
    }


    companion object {
        var created = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        created = true


        if(!MainActivity.debugNoVolume) {

            mAudioManager = getSystemService(AUDIO_SERVICE) as AudioManager
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && notificationManager.isNotificationPolicyAccessGranted) {
                mAudioManager.ringerMode = AudioManager.RINGER_MODE_NORMAL
            }
            var maxVolume: Int = mAudioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            mAudioManager.setStreamVolume(
                AudioManager.STREAM_MUSIC,
                maxVolume,
                AudioManager.FLAG_SHOW_UI
            )


        }

        mp = MediaPlayer.create(this, R.raw.alarm)
        mp.isLooping = true
        data = Data().loadData()
        pass = data.password
        timerStopped = false

        for (i in 0 until dataObj.contactList.size) {
            if (dataObj.contactList[i] != "") {
                var number = dataObj.contactList[i]
                listNumber.add(number)


            }

        }
        //if(!mp.isPlaying) mp!!.start()


        setTimer()

        locationT.text = LocalisationService.location
        button0.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("0")
                checkPassword()
            }
        }

        button1.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("1")
                checkPassword()
            }
        }

        button2.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("2")
                checkPassword()
            }
        }

        button3.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("3")
                checkPassword()
            }
        }

        button4.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("4")
                checkPassword()
            }
        }

        button5.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("5")
                checkPassword()
            }
        }

        button6.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("6")
                checkPassword()
            }
        }

        button7.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("7")
                checkPassword()

            }
        }

        button8.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("8")
                checkPassword()
            }
        }


        button9.setOnClickListener {

            if (password.length() < 4) {
                password.text = password.text.toString().plus("9")
                checkPassword()
            }
        }

        deleteChar.setOnClickListener {
            if (password.length() > 0) {
                password.text = password.text.toString().dropLast(1)
            }

        }


    }


    private fun setTimer(){
         object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timer.text = (millisUntilFinished / 1000).toString()
            }

            override fun onFinish() {
                if(!timerStopped) {
                    timer.text = "Contacts warned"
                    contactRelatives()
                }
            }
        }.start()

    }
    private fun checkPassword() {

        if (password.text == pass) {
            mp!!.stop()
            ShakeService.alarmActivated = false
            created = false
            timerStopped = true
            Toast.makeText(this, "Alert stopped", Toast.LENGTH_LONG).show()

            this.finish()
        }
    }

    override fun onBackPressed() {
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, "Please use your password", duration)
        toast.show()

    }


    private fun sendNotification() {


        val topic = "/topics/alert" //topic has to match what the receiver subscribed to

        val notification = JSONObject()
        val notifcationBody = JSONObject()

        try {
            notifcationBody.put("title", "WARNING : AGRESSION NEAR YOU")
            notifcationBody.put("latitude", LocalisationService.latitude)   //Enter your notification message
            notifcationBody.put("longitude", LocalisationService.longitude)
            notification.put("to", topic)
            notification.put("data", notifcationBody)
        } catch (e: JSONException) {
        }



        val jsonObjectRequest = object : JsonObjectRequest(FCM_API, notification,
            Response.Listener<JSONObject> { response ->
                println("onResponse: $response")

            },
            Response.ErrorListener {
                Toast.makeText(this@AlertActivity, "Request error", Toast.LENGTH_LONG).show()
                println("onErrorResponse: Didn't work")
            }) {

            override fun getHeaders(): Map<String, String> {
                val params = HashMap<String, String>()
                params["Authorization"] = serverKey
                params["Content-Type"] = contentType
                return params
            }
        }
        requestQueue.add(jsonObjectRequest)
    }


    private fun sendSMS(phoneNo: String?, msg: String?) {
        try {
            val smsManager: SmsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNo, null, msg, null, null)
            Toast.makeText(
                applicationContext, "Message Sent",
                Toast.LENGTH_LONG
            ).show()
        } catch (ex: Exception) {
            Toast.makeText(
                applicationContext, ex.message.toString(),
                Toast.LENGTH_LONG
            ).show()
            ex.printStackTrace()
        }
    }


    private fun contactRelatives() {

        val toast = Toast.makeText(this, "${listNumber.size} relatives contacted", Toast.LENGTH_LONG)


        //Couldn't try that because of our european mobile phone plans
        /*
        for(i in 0 until listNumber.size-1){
            if(listNumber[i] != "") {
                sendSMS(listNumber[i], "I have been agressed here : latitude : ${LocalisationService.latitude}, longitude : ${LocalisationService.longitude}, can you help me ?")
            }

        }

        */

        toast.show()
        sendNotification()

    }
}
