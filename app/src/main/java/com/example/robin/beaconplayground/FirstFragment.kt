package com.example.robin.beaconplayground

import android.graphics.Color
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.first_frag.*

class FirstFragment : Fragment() {


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.first_frag, container, false)

        val colorString = arguments.getString("msg")
        val color = when (colorString) {
            "Blue" -> Color.BLUE
            else -> Color.RED
        }

        view?.setBackgroundColor(color)

        infoText.text = colorString

        return view
    }

    companion object {
        fun newInstance(text: String): FirstFragment {
            val fragment = FirstFragment()

            val bundle = Bundle()
            bundle.putString("msg", text)

            fragment.arguments = bundle
            return fragment
        }
    }
}