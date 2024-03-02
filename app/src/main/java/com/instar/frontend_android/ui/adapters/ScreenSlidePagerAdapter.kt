package com.instar.frontend_android.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.instar.frontend_android.ui.fragments.CameraFragment
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.MessengerFragment

class ScreenSlidePagerAdapter(adapter: FragmentActivity) : FragmentStateAdapter(adapter) {
    private val TAB_COUNT = 3
    override fun getItemCount(): Int = TAB_COUNT
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> CameraFragment()
            1 -> HomeFragment()
            else -> MessengerFragment()
        }
    }
}