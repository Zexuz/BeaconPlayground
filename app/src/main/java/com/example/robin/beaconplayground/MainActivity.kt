package com.example.robin.beaconplayground

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.annotation.RequiresApi
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.altbeacon.beacon.*
import org.altbeacon.beacon.service.RangedBeacon

class MainActivity : FragmentActivity(), BeaconConsumer {

    private lateinit var _beaconManager: BeaconManager

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        turnOffBluetoothButton.setOnClickListener({
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (!adapter.isEnabled)
                adapter.enable()
        })

        viewPager.adapter = MyPageAdapter(supportFragmentManager)

        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            val alert = AlertDialog.Builder(this)
            alert.setTitle("Needs location")
            alert.setPositiveButton("Nice!", null)
            alert.show()
        }

        RangedBeacon.setSampleExpirationMilliseconds(5000) //https://stackoverflow.com/questions/25520713/how-to-get-faster-ranging-responses-with-altbeacon
        _beaconManager = BeaconManager.getInstanceForApplication(this)
        _beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))

        _beaconManager.bind(this)
        registerReceiver(getListener(), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    private fun getTextView(): TextView {
        return fragInfoText
    }

    override fun onResume() {
        super.onResume()
        verifyBluetoothState(BluetoothAdapter.getDefaultAdapter().state)
    }

    override fun onDestroy() {
        super.onDestroy()
        _beaconManager.unbind(this)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBeaconServiceConnect() {
        _beaconManager.addRangeNotifier({ beacons: MutableCollection<Beacon>, region: Region ->
            if (beacons.size > 0) {
                val distance = "The first beacon I see is about ${"%.2f".format(beacons.iterator().next().distance)} meters away"
                    runOnUiThread({
                        getTextView().text = distance
                        viewPager.currentItem = if (beacons.iterator().next().distance >2) 1 else 0
                    })
                Log.i("Beacon distance", distance)
            }
        })

        try {
            _beaconManager.startRangingBeaconsInRegion(Region("eff799ba-e5bc-493b-9995-10a38bedc784", null, null, null))
        } catch (e: Exception) {
        }
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

