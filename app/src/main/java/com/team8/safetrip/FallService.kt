package com.team8.safetrip

import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import weka.core.Attribute
import weka.core.FastVector
import weka.core.Instance
import weka.core.Instances
import java.io.BufferedReader
import kotlin.math.pow
import kotlin.math.sqrt
import java.util.*


class FallService : Service(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private var mGyroscope: Sensor? = null
    private var mAccelerometer: Sensor? = null
    private var mLinearAcceleration: Sensor? = null
    private var mGravity: Sensor? = null
    private var mRotationVector: Sensor? = null
    private val numFeature = 96
    private val listFeature = List(numFeature) { i -> "f$i" } //{"f0", "f1", "f2", "f3"}
    private val listClass = arrayOf("other", "fall", "run")
    private var instances = createEmptyInstances()
    private var classIndex = 0
    private val classifier = ClassifierWrapper()

    private var accelerometerX: MutableList<Float> = ArrayList()
    private var accelerometerY: MutableList<Float> = ArrayList()
    private var accelerometerZ: MutableList<Float> = ArrayList()
    private var gyroscopeX: MutableList<Float> = ArrayList()
    private var gyroscopeY: MutableList<Float> = ArrayList()
    private var gyroscopeZ: MutableList<Float> = ArrayList()
    private var gravityX: MutableList<Float> = ArrayList()
    private var gravityY: MutableList<Float> = ArrayList()
    private var gravityZ: MutableList<Float> = ArrayList()
    private var linearX: MutableList<Float> = ArrayList()
    private var linearY: MutableList<Float> = ArrayList()
    private var linearZ: MutableList<Float> = ArrayList()
    private var rotation0: MutableList<Float> = ArrayList()
    private var rotation1: MutableList<Float> = ArrayList()
    private var rotation2: MutableList<Float> = ArrayList()
    private var rotation3: MutableList<Float> = ArrayList()

    companion object {

        var INSTANCE: FallService? = null

    }


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        super.onCreate()

        INSTANCE = this
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        mLinearAcceleration = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        mGravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
        mRotationVector = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)

        mGyroscope?.also { gyroscope ->
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME)
        }
        mAccelerometer?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME)
        }
        mLinearAcceleration?.also { linear ->
            sensorManager.registerListener(this, linear, SensorManager.SENSOR_DELAY_GAME)
        }
        mGravity?.also { gravity ->
            sensorManager.registerListener(this, gravity, SensorManager.SENSOR_DELAY_GAME)
        }
        mRotationVector?.also { rotation ->
            sensorManager.registerListener(this, rotation, SensorManager.SENSOR_DELAY_GAME)
        }

        loadInstances()
        if(!MainActivity.launchedAll) Toast.makeText(this,"Fall service launched", Toast.LENGTH_SHORT).show()
        return START_STICKY


    }

    override fun onDestroy() {
        super.onDestroy()
        INSTANCE = null
        sensorManager.unregisterListener(this)
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        // Do something here if sensor accuracy changes.
    }

    override fun onSensorChanged(event: SensorEvent) {
        val sensor = event.sensor
        if (sensor.type == Sensor.TYPE_ACCELEROMETER) {
            accelerometerX.add(event.values[0])
            accelerometerY.add(event.values[1])
            accelerometerZ.add(event.values[2])
        }
        if (sensor.type == Sensor.TYPE_GYROSCOPE) {
            gyroscopeX.add(event.values[0])
            gyroscopeY.add(event.values[1])
            gyroscopeZ.add(event.values[2])
        }
        if (sensor.type == Sensor.TYPE_GRAVITY) {
            gravityX.add(event.values[0])
            gravityY.add(event.values[1])
            gravityZ.add(event.values[2])
        }
        if (sensor.type == Sensor.TYPE_LINEAR_ACCELERATION) {
            linearX.add(event.values[0])
            linearY.add(event.values[1])
            linearZ.add(event.values[2])
        }
        if (sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
            rotation0.add(event.values[0])
            rotation1.add(event.values[1])
            rotation2.add(event.values[2])
            rotation3.add(event.values[3])
        }
        if (gyroscopeX.size >= 100 && accelerometerX.size >= 100 && gravityX.size >= 100 && linearX.size >= 100 && rotation0.size >= 100) {
            val instance = createInstance()
            instance.setDataset(instances)
            val predictedClass = classifier.predict(instance)
            //Log.d("FALLSERVICE", "predicted class: $predictedClass")
            if ((predictedClass == "fall" || predictedClass == "run") && !AlertActivity.created)
                ring()
            clearData()
        }
    }

    private fun ring()
    {
        sendMessageToActivity("Alarm")

    }

    private fun sendMessageToActivity(msg: String) {


        val i = Intent("intentKey")
        i.putExtra("key", msg)
        LocalBroadcastManager.getInstance(this).sendBroadcast(i)
    }


//------------------------------------------------------------------------------------------------------------------------
//########################################################################################################################
//------------------------------------------------------------------------------------------------------------------------

    // Create an empty list of instances
    private fun createEmptyInstances(): Instances {
        val attrs = FastVector()
        for (f in listFeature)
            attrs.addElement(Attribute(f))

        val classes = FastVector()
        for (c in listClass)
            classes.addElement(c)

        attrs.addElement(Attribute("label", classes))
        return Instances("myInstances", attrs, 10000)
    }

    //Create a single instance, which consists 30 features and the class label
    private fun createInstance(): Instance {
        val values = doubleArrayOf(
            accelerometerX.max()!!.toDouble(), accelerometerY.max()!!.toDouble(), accelerometerZ.max()!!.toDouble(),
            accelerometerX.min()!!.toDouble(), accelerometerY.min()!!.toDouble(), accelerometerZ.min()!!.toDouble(),
            accelerometerX.average(), accelerometerY.average(), accelerometerZ.average(),
            variance(accelerometerX), variance(accelerometerY), variance(accelerometerZ),
            standardDeviation(accelerometerX), standardDeviation(accelerometerY), standardDeviation(accelerometerZ),
            median(accelerometerX), median(accelerometerY), median(accelerometerZ),
            gyroscopeX.max()!!.toDouble(), gyroscopeY.max()!!.toDouble(), gyroscopeZ.max()!!.toDouble(),
            gyroscopeX.min()!!.toDouble(), gyroscopeY.min()!!.toDouble(), gyroscopeZ.min()!!.toDouble(),
            gyroscopeX.average(), gyroscopeY.average(), gyroscopeZ.average(),
            variance(gyroscopeX), variance(gyroscopeY), variance(gyroscopeZ),
            standardDeviation(gyroscopeX), standardDeviation(gyroscopeY), standardDeviation(gyroscopeZ),
            median(gyroscopeX), median(gyroscopeY), median(gyroscopeZ),
            gravityX.max()!!.toDouble(), gravityX.max()!!.toDouble(), gravityX.max()!!.toDouble(),
            gravityX.min()!!.toDouble(), gravityX.min()!!.toDouble(), gravityX.min()!!.toDouble(),
            gravityX.average(), gravityY.average(), gravityZ.average(),
            variance(gravityX), variance(gravityY), variance(gravityZ),
            standardDeviation(gravityX), standardDeviation(gravityY), standardDeviation(gravityZ),
            median(gravityX), median(gravityY), median(gravityZ),
            linearX.max()!!.toDouble(), linearX.max()!!.toDouble(), linearX.max()!!.toDouble(),
            linearX.min()!!.toDouble(), linearX.min()!!.toDouble(), linearX.min()!!.toDouble(),
            linearX.average(), linearY.average(), linearZ.average(),
            variance(linearX), variance(linearY), variance(linearZ),
            standardDeviation(linearX), standardDeviation(linearY), standardDeviation(linearZ),
            median(linearX), median(linearY), median(linearZ),
            rotation0.max()!!.toDouble(), rotation1.max()!!.toDouble(), rotation2.max()!!.toDouble(), rotation3.max()!!.toDouble(),
            rotation0.min()!!.toDouble(), rotation1.min()!!.toDouble(), rotation2.min()!!.toDouble(), rotation3.min()!!.toDouble(),
            rotation0.average(), rotation1.average(), rotation2.average(), rotation3.average(),
            variance(rotation0), variance(rotation1), variance(rotation2), variance(rotation3),
            standardDeviation(rotation0), standardDeviation(rotation1), standardDeviation(rotation2), standardDeviation(rotation3),
            median(rotation0), median(rotation1), median(rotation2), median(rotation3))

        val attrClass = instances.attribute("label")
        val instance = Instance(numFeature + 1)
        for (j in 0 until numFeature) {
            val attr = instances.attribute(listFeature[j])
            instance.setValue(attr, values[j])
        }
        instance.setValue(attrClass, listClass[classIndex])
        return instance
    }

    private fun loadInstances() {
        val assetManager = resources.assets
        instances = Instances(BufferedReader(assetManager.open("myModel.arff").bufferedReader()))
        val attrClass = instances.attribute("label")
        instances.setClass(attrClass)
        classifier.train(instances)
    }

    private fun variance(value: MutableList<Float>): Double {
        val mean = value.average().toFloat()
        val tempList = value.toMutableList()
        for (i in 0 until tempList.size) {
            tempList[i] = tempList[i] - mean
            tempList[i] = tempList[i].pow(2F)
        }
        return (tempList.sum()/(tempList.size-1)).toDouble()
    }

    private fun standardDeviation(value: MutableList<Float>): Double {
        return sqrt(variance(value))
    }

    private fun median(value: MutableList<Float>): Double {
        value.sort()
        return if (value.size%2 != 0) {
            val  middle = value.size/2
            ((value[middle] + value[middle-1])/2).toDouble()
        } else {
            value[(value.size-1)/2].toDouble()
        }
    }

    private fun clearData() {
        accelerometerX.clear()
        accelerometerY.clear()
        accelerometerZ.clear()
        gyroscopeX.clear()
        gyroscopeY.clear()
        gyroscopeZ.clear()
        gravityX.clear()
        gravityY.clear()
        gravityZ.clear()
        linearX.clear()
        linearY.clear()
        linearZ.clear()
        rotation0.clear()
        rotation1.clear()
        rotation2.clear()
        rotation3.clear()
    }
}
