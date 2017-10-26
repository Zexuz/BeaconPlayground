package com.example.robin.beaconplayground

import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast

class MainActivity : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val button = findViewById<Button>(R.id.button2)
        button.setOnClickListener({
            val adapter = BluetoothAdapter.getDefaultAdapter()
            if (adapter.isEnabled)
                adapter.disable()
            else
                adapter.enable()
        })

        val pager = findViewById<ViewPager>(R.id.viewPager)
        pager.adapter = MyPagerAdapter(supportFragmentManager)

        registerReceiver(getListener(), IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED))
    }

    class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {
        override fun getCount(): Int {
            return 2
        }

        override fun getItem(position: Int): Fragment {
            return when (position) {
                0 -> FirstFragment.newInstace("Blue")
                else -> FirstFragment.newInstace("Red")
            }
        }
    }

    class FirstFragment : Fragment() {


        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
            val view = inflater?.inflate(R.layout.first_frag, container, false)

            val textView = view?.findViewById<TextView>(R.id.infoText)

            val colorString = arguments.getString("msg")
            val color = when (colorString) {
                "Blue" -> Color.BLUE
                else -> Color.RED
            }

            view?.setBackgroundColor(color)

            textView?.text = colorString

            return view
        }

        companion object {
            fun newInstace(text: String): FirstFragment {
                val fragment = FirstFragment()

                val bundle = Bundle()
                bundle.putString("msg", text)

                fragment.arguments = bundle
                return fragment
            }
        }
    }

    private fun getTextView(): TextView {
        return findViewById<TextView>(R.id.infoText) as TextView
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

