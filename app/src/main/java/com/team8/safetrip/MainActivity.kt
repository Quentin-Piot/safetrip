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
        loadData()
        val intent = Intent(this, ShakeService::class.java)


        startService(intent)


        settingsButton.setOnClickListener{

            val intentSettings = Intent(this, Settings::class.java)


            startActivity(intentSettings)

            println("start")

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


    fun loadData(){
        try {
            val fis: FileInputStream = this.openFileInput("test.test")
            val `is` = ObjectInputStream(fis)
            val data: Data = `is`.readObject() as Data
            `is`.close()
            fis.close()
            print("loaded")
        }catch (e : Exception){
        println(e.message)
        }
    }



}







