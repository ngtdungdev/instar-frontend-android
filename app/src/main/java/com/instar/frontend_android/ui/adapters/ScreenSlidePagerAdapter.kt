package com.instar.frontend_android.ui.adapters

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.instar.frontend_android.ui.activities.MainScreenActivity
import com.instar.frontend_android.ui.fragments.HomeFragment
import com.instar.frontend_android.ui.fragments.MessengerFragment
import com.instar.frontend_android.ui.fragments.PostFragment

class ScreenSlidePagerAdapter(adapter: FragmentActivity) : FragmentStateAdapter(adapter) {

    private var onItemClickListener: HomeFragment.OnItemClickListener? = null
    init {
        if (adapter is HomeFragment.OnItemClickListener) {
            onItemClickListener = adapter
        }
    }
    private val TAB_COUNT = 3
    override fun getItemCount(): Int = TAB_COUNT
    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> PostFragment()
            1 -> HomeFragment().apply {
                onItemClickListener?.let { setOnItemClickListener(it) }
            }
            else -> MessengerFragment()
        }
    }
}