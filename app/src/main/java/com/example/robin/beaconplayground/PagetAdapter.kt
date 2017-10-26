package com.example.robin.beaconplayground

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter

class MyPagerAdapter(fm: FragmentManager) : FragmentPagerAdapter(fm) {

    private var dict: HashMap<Int, Fragment>

    init {
        dict = hashMapOf(
                0 to FirstFragment.newInstance("Blue"),
                1 to FirstFragment.newInstance("Red")
        )
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getItem(position: Int): Fragment {
        return dict[0] as Fragment
    }
}