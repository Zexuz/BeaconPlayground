package com.example.robin.beaconplayground

import android.Manifest
import android.app.AlertDialog
import android.bluetooth.BluetoothAdapter
import android.content.*
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*
import org.altbeacon.beacon.*
import org.altbeacon.beacon.service.RangedBeacon

class MainActivity : FragmentActivity(), BeaconConsumer {

    private lateinit var _beaconManager: BeaconManager

    private lateinit var ourBeacons:List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ourBeacons = listOf("aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaaa","bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbbb")
        
        turnOffBluetoothButton.setOnClickListener({
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter.isEnabled)
                adapter.disable()
        })

        seekBar.setOnSeekBarChangeListener(SeekBarListener(seekbarValue))


        getTextView().text = "Searching for beacon!"
        viewPager.adapter = MyPageAdapter(supportFragmentManager)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                val alert = AlertDialog.Builder(this)
                alert.setTitle("Needs location")
                alert.setPositiveButton("Nice!", null)
                alert.show()
            }
            //todo add that we request permission
        }

        uuid.setOnEditorActionListener(TextView.OnEditorActionListener { textView, i, keyEvent ->
            if (i == EditorInfo.IME_ACTION_DONE) {
                Toast.makeText(this, "Now searching for ${uuid.text}", Toast.LENGTH_SHORT).show()
                _beaconManager.startRangingBeaconsInRegion(Region(uuid.text.toString(), null, null, null))
                return@OnEditorActionListener true
            }
            return@OnEditorActionListener false
        })


        RangedBeacon.setSampleExpirationMilliseconds(2000) //https://stackoverflow.com/questions/25520713/how-to-get-faster-ranging-responses-with-altbeacon
        _beaconManager = BeaconManager.getInstanceForApplication(this)
        _beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout(BeaconParser.ALTBEACON_LAYOUT))
        _beaconManager.beaconParsers.add(BeaconParser().setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24")) //Ibeacon

        _beaconManager.bind(this)
        registerReceiver(getListener(), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    class SeekBarListener(val textView: TextView) : SeekBar.OnSeekBarChangeListener {

        override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
            textView.text = p1.toString()
        }

        override fun onStartTrackingTouch(p0: SeekBar?) {
        }

        override fun onStopTrackingTouch(p0: SeekBar?) {
        }


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

    override fun onBeaconServiceConnect() {
        _beaconManager.addRangeNotifier({ beacons: MutableCollection<Beacon>, region: Region ->
            if (!beacons.isNotEmpty()) return@addRangeNotifier

            if (beacons.toList().find{it.id1.toString().trim() == ourBeacons.first().toString().trim() } == null) return@addRangeNotifier

            val distance = "The first beacon I see is about ${"%.2f".format(beacons.iterator().next().distance)} meters away"
            runOnUiThread({
                getTextView().text = distance
                viewPager.currentItem = if (beacons.iterator().next().distance > seekBar.progress) 1 else 0
            })
            Log.i("Beacon distance", distance)
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
            val intent = Intent(applicationContext, BluetoothActivity::class.java)
            startActivity(intent)
            return
        }
    }

}

