package com.kamikaze.rotatelayout

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.view.OrientationEventListener
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    private val listener: OrientationEventListener by lazy {
        object: OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            override fun onOrientationChanged(orientation: Int) {
                findViewById<RotateLayout>(R.id.rotateLayout).setOrientation(orientation, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }


    override fun onStart() {
        super.onStart()

        if (listener.canDetectOrientation()) {
            listener.enable()
        }
    }

    override fun onStop() {
        super.onStop()
        listener.disable()
    }
}