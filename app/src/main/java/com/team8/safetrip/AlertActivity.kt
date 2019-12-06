package com.team8.safetrip

import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alert.*


class AlertActivity : AppCompatActivity() {

    private lateinit var mp : MediaPlayer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)
        mp = MediaPlayer.create(this, R.raw.alarm)
        mp.isLooping = true
        if(!mp.isPlaying) mp!!.start()


        setTimer()

        locationT.text = "Location : " + LocalisationService.latitude
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
                timer.text = "Contacts warned"
                contactRelatives()
            }
        }.start()

    }
    private fun checkPassword() {
        val data = Data().loadData()
        val pass = data.password
        if (password.text == pass) {

            ShakeService.alarmActivated = false
            this.finish()
        }
    }

    override fun onBackPressed() {
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, "Please use your password", duration)
        toast.show()

    }


    private fun contactRelatives(){



    }
}
