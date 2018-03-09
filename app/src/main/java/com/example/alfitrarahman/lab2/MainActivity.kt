package com.example.alfitrarahman.lab2

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.hardware.SensorManager
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.longToast
import org.jetbrains.anko.toast
import android.media.MediaPlayer
import java.io.IOException
import java.lang.Integer.parseInt


class MainActivity : AppCompatActivity(), SensorEventListener, MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener {
    private lateinit var mSensorManager: SensorManager
    private var mSensor: Sensor? = null
    private var thresholdValue = 60
    private var mMediaPlayer: MediaPlayer? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mSensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (mSensorManager?.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null) {
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
            thresholdText.setText(thresholdValue.toString())
        } else {
            val thresholdSound = MediaPlayer.create(this@MainActivity, R.raw.airplane)
            thresholdSound.start()
            longToast("Magnetometer Sensor is not available in your phone")
        }
        resetThreshold.setOnClickListener{
            var newThreshold = 0
            var format = true
            try {
                newThreshold = parseInt(thresholdText.text.toString())
            }catch (e: NumberFormatException){
                format = false
            }
            if (format){
                thresholdValue = newThreshold
                toast("Success!")
            }else{
                toast("Incorrect Input!")
            }
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event!=null) {
            val magnitudeX = event.values[0].toDouble()
            val magnitudeY = event.values[1].toDouble()
            val magnitudeZ = event.values[2].toDouble()
            value_x_text.text = magnitudeX.toInt().toString()
            value_y_text.text = magnitudeY.toInt().toString()
            value_z_text.text = magnitudeZ.toInt().toString()

            val magnitudeResult = Math.sqrt((magnitudeX * magnitudeX) + (magnitudeY * magnitudeY) + (magnitudeZ * magnitudeZ))
            result_text.text = (magnitudeResult.toInt().toString())

            if (magnitudeResult > thresholdValue){
                try {
                    mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.airplane)
                    mMediaPlayer?.setOnPreparedListener(this)
                    mMediaPlayer?.setOnCompletionListener(this)

                }catch (e: IOException){
                    mMediaPlayer?.setOnErrorListener(this)
                }
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onStop() {
        super.onStop()
        mSensorManager.unregisterListener(this)

    }

    override fun onPrepared(player: MediaPlayer){
        player.start()
    }

    override fun onError(player: MediaPlayer, p1: Int, p2: Int): Boolean {
        mMediaPlayer?.reset()
        mMediaPlayer = MediaPlayer.create(this@MainActivity, R.raw.airplane)
        return true
    }

    override fun onCompletion(player: MediaPlayer?) {
        player?.release()
    }
}
