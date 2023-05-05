package com.app.musicplayer.ui.adapters

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    fragmentManager: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int = 5

    private var fragmentList = arrayListOf<Fragment>()

    @RequiresApi(Build.VERSION_CODES.N)
    override fun createFragment(position: Int): Fragment = fragmentList[position]


    fun addFragment(fragment: Fragment) {
        fragmentList.add(fragment)
    }

}