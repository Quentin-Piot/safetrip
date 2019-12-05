package com.example.falldetection


import android.app.Service
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.IBinder
import android.util.Log
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
    private val numFeature = 30
    private val listFeature = List(numFeature) { i -> "f$i" } //{"f0", "f1", "f2", "f3"}
    private val listClass = arrayOf("other", "fall")
    private var instances = createEmptyInstances()
    private var classIndex = 0
    private val classifier = ClassifierWrapper()

    private var accelerometerX: MutableList<Float> = ArrayList()
    private var accelerometerY: MutableList<Float> = ArrayList()
    private var accelerometerZ: MutableList<Float> = ArrayList()
    private var gyroscopeX: MutableList<Float> = ArrayList()
    private var gyroscopeY: MutableList<Float> = ArrayList()
    private var gyroscopeZ: MutableList<Float> = ArrayList()

    private val myTag = "InsideService"

    override fun onBind(intent: Intent): IBinder? {
        Log.d(myTag, "onBind entered")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(myTag, "onCreate entered")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mGyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
        mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

        mGyroscope?.also { gyroscope ->
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL)
        }
        mAccelerometer?.also { accelerometer ->
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }

        loadInstances()

    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(myTag, "onDestroyed entered")
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
        if (gyroscopeX.size >= 10 && accelerometerX.size >= 10) {
            val instance = createInstance()
            instance.setDataset(instances)
            val predictedClass = classifier.predict(instance)
            Log.d(myTag, "Prediction successful. Result: $predictedClass")
            if (predictedClass == "fall")
                broadcast()
            clearData()
        }
    }

    private fun broadcast()
    {
        Log.d(myTag, "broadcast entered")
        val intent = Intent()
        intent.action = "testing_action"
        sendBroadcast(intent)
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
            accelerometerX.max()!!.toDouble(), accelerometerX.max()!!.toDouble(), accelerometerX.max()!!.toDouble(),
            accelerometerX.min()!!.toDouble(), accelerometerX.min()!!.toDouble(), accelerometerX.min()!!.toDouble(),
            accelerometerX.average(), accelerometerY.average(), accelerometerZ.average(),
            variance(accelerometerX), variance(accelerometerY), variance(accelerometerZ),
            standardVariation(accelerometerX), standardVariation(accelerometerY), standardVariation(accelerometerZ),
            gyroscopeX.max()!!.toDouble(), gyroscopeY.max()!!.toDouble(), gyroscopeZ.max()!!.toDouble(),
            gyroscopeX.min()!!.toDouble(), gyroscopeY.min()!!.toDouble(), gyroscopeZ.min()!!.toDouble(),
            gyroscopeX.average(), gyroscopeY.average(), gyroscopeZ.average(),
            variance(gyroscopeX), variance(gyroscopeY), variance(gyroscopeZ),
            standardVariation(gyroscopeX), standardVariation(gyroscopeY), standardVariation(gyroscopeZ))

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
       // instances = Instances(BufferedReader(assetManager.open("myModel.arff").bufferedReader()))
        instances = Instances(BufferedReader(assetManager.open("shake_gives_fall.arff").bufferedReader()))
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

    private fun standardVariation(value: MutableList<Float>): Double {
        return sqrt(variance(value))
    }

    private fun clearData() {
        accelerometerX.clear()
        accelerometerY.clear()
        accelerometerZ.clear()
        gyroscopeX.clear()
        gyroscopeY.clear()
        gyroscopeZ.clear()
    }
}
