package com.example.robin.beaconplayground

import android.bluetooth.BluetoothAdapter
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.Toast


internal class BluetoothActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_turnonbluetooth)

        val button = findViewById<Button>(R.id.button)
        button.setOnClickListener({
            turnOnBluetooth()
        })

    }

    private fun turnOnBluetooth() {
        BluetoothAdapter.getDefaultAdapter().enable()
        runOnUiThread({
            Toast.makeText(applicationContext, "I turned on bluetooth for you!", Toast.LENGTH_SHORT).show()
        })
        finishFromChild(this)
    }

}