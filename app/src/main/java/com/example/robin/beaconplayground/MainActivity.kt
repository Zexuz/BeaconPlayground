package com.example.robin.beaconplayground

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val textView = findViewById<TextView>(R.id.infoText)

        registerReceiver(getListener(textView), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun getListener(textView: TextView): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (intent?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return

                val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                textView.text = "Bluetooth is turned ${getStateText(state)}"
            }
        }
    }

    private fun getStateText(i: Int): String = when (i) {
        BluetoothAdapter.STATE_ON -> "on"
        BluetoothAdapter.STATE_OFF -> "off"
        else -> "Error"
    }

}

