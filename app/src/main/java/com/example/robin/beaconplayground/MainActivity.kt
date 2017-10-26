package com.example.robin.beaconplayground

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        turnOffBluetoothButton.setOnClickListener({
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (!adapter.isEnabled)
                adapter.enable()
        })

        viewPager.adapter = MyPageAdapter(supportFragmentManager)

        registerReceiver(getListener(), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun getTextView(): TextView {
        return fragInfoText
    }

    override fun onResume() {
        super.onResume()
        verifyBluetoothState(BluetoothAdapter.getDefaultAdapter().state)
    }

    private fun getListener(): BroadcastReceiver {
        return object : BroadcastReceiver() {
            override fun onReceive(p0: Context?, p1: Intent?) {
                if (p1?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return

                val state = p1.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

                if (state == BluetoothAdapter.ERROR) {
                    runOnUiThread({
                        Toast.makeText(baseContext, "Error bluetooth state", Toast.LENGTH_SHORT).show()
                    })
                    return
                }

                verifyBluetoothState(state)
            }
        }
    }

    private fun verifyBluetoothState(state: Int) {

        if (state == BluetoothAdapter.STATE_OFF) {
            setText(false)
            val intent = Intent(applicationContext, BluetoothActivity::class.java)
            startActivity(intent)
            return
        }

        setText(true)
    }


    private fun setText(isTurnedOn: Boolean) {
        val textView = getTextView()

        textView.text = if (isTurnedOn)
            resources.getString(R.string.bluetooth_status_on) else
            resources.getString(R.string.bluetooth_status_off)
    }

}

