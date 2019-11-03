package com.ingeance.safetrip
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AppCompatActivity

import com.ingeance.safetrip.ShakeDetector


class MainActivity : AppCompatActivity() {

    private var mSensorManager: SensorManager? = null
    private var mAccelerometer: Sensor? = null
    private var mShakeDetector: ShakeDetector? = null

    private var tvShake: TextView? = null
    private var btn: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {   //demo link   http://jasonmcreynolds.com/?p=388
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvShake = findViewById(R.id.tvShake)
        btn = findViewById(R.id.btn)

        btn!!.setOnClickListener {
            val intent = Intent(this@MainActivity, ServiceActivity::class.java)
            startActivity(intent)
        }

        // ShakeDetector initialization
        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager!!
            .getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mShakeDetector = ShakeDetector()
        mShakeDetector!!.setOnShakeListener(object : ShakeDetector.OnShakeListener {

            override fun onShake(count: Int) {
                /*
                 * The following method, "handleShakeEvent(count):" is a stub //
                 * method you would use to setup whatever you want done once the
                 * device has been shook.
                 */
                tvShake!!.text = "Shake Action is just detected!!"
                Toast.makeText(this@MainActivity, "Shaked!!!", Toast.LENGTH_SHORT).show()
            }
        })

    }

    public override fun onResume() {
        super.onResume()
        // Add the following line to register the Session Manager Listener onResume
        mSensorManager!!.registerListener(
            mShakeDetector,
            mAccelerometer,
            SensorManager.SENSOR_DELAY_UI
        )
    }

    public override fun onPause() {
        // Add the following line to unregister the Sensor Manager onPause
        mSensorManager!!.unregisterListener(mShakeDetector)
        super.onPause()
    }
}