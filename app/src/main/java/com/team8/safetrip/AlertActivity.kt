package com.team8.safetrip

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_alert.*

class AlertActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alert)



        button0.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("0")
            }
        }

        button1.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("1")
            }
        }

        button2.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("2")
            }
        }

        button3.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("3")
            }
        }

        button4.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("4")
            }
        }

        button5.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("5")
            }
        }

        button6.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("6")
            }
        }

        button7.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("7")
            }
        }

        button8.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("8")
            }
        }


        button9.setOnClickListener {

            if(password.length() < 4){
                password.text = password.text.toString().plus("9")
            }
        }

        deleteChar.setOnClickListener{
            if(password.length() > 0){
                password.text = password.text.toString().dropLast(1)
            }

        }



    }
}
