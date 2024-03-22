package com.instar.frontend_android.ui.adapters

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.MessengerFragment
import com.instar.frontend_android.ui.fragments.PostFragment

class ScreenSlidePagerAdapter(
    fragmentActivity: FragmentActivity,
) : FragmentStateAdapter(fragmentActivity) {

    private val TAB_COUNT = 3

    override fun getItemCount(): Int = TAB_COUNT

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> PostFragment()
            1 -> HomeFragment()
            else -> MessengerFragment()
        }
        fragment.arguments = Bundle().apply {
            putInt("position", position)
        }
        return fragment
    }
}
