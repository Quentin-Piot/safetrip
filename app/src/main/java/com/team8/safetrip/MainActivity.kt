package com.team8.safetrip


import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*



class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
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


    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        val permission2 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_EXTERNAL_STORAGE
        )

        val permission3 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.SEND_SMS
        )


        val permission4 = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.READ_CONTACTS
        )



        if (permission != PackageManager.PERMISSION_GRANTED || permission2 != PackageManager.PERMISSION_GRANTED || permission3 != PackageManager.PERMISSION_GRANTED || permission4 != PackageManager.PERMISSION_DENIED) {
            //  Log.i(TAG, "Permission to record denied")
            makeRequest()
        }
    }


    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.SEND_SMS,
                Manifest.permission.READ_CONTACTS
            ),
            1
        )

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[1] != PackageManager.PERMISSION_GRANTED || grantResults[2] != PackageManager.PERMISSION_GRANTED || grantResults[3] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(applicationContext, "Please accept all permissions", Toast.LENGTH_SHORT).show()

                    setupPermissions()
                } else {

                    Toast.makeText(applicationContext, "All permissions granted", Toast.LENGTH_SHORT).show()

                }
            }
        }
    }


}






