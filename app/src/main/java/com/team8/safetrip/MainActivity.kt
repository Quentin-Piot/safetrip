package com.team8.safetrip


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import java.io.FileInputStream
import java.io.ObjectInputStream
import java.lang.Exception


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this, ShakeService::class.java)


        startService(intent)


        settingsButton.setOnClickListener {

            val intentSettings = Intent(this, Settings::class.java)
            startActivityForResult(intentSettings, 0)


        }


    }


    private val br = object : BroadcastReceiver() {

        override fun onReceive(context: Context, intent: Intent) {

            val bundle = intent.extras
            if (bundle != null) {


            }
        }
    }

    override fun onResume() {
        // TODO Auto-generated method stub
        super.onResume()
        registerReceiver(br, IntentFilter("1"))

    }

    override fun onPause() {
        // TODO Auto-generated method stub
        super.onPause()
        unregisterReceiver(br)
    }


}







