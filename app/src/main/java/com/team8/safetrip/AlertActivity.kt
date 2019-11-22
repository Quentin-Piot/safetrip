package com.team8.safetrip

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_alert.*


class AlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)



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

    private fun checkPassword() {
        if (password.text == "0000") {

            ShakeService.mp.stop()
            this.finish()
        }
    }

    override fun onBackPressed() {
        val text = "Hello toast!"
        val duration = Toast.LENGTH_SHORT

        val toast = Toast.makeText(applicationContext, "Please use your password", duration)
        toast.show()

    }
}
