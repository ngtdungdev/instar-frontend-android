package com.instar.frontend_android.ui.activities
//vuốt sang
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.viewpager2.widget.ViewPager2
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityMainScreenBinding
import com.instar.frontend_android.ui.adapters.ScreenSlidePagerAdapter

class MainScreenActivity : AppCompatActivity() {
    private lateinit var biding : ActivityMainScreenBinding
    private lateinit var viewPager : ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(biding.root)
        viewPager = biding.viewPager
        viewPager.adapter = ScreenSlidePagerAdapter(this)
        viewPager.setCurrentItem(1, true)
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                if (positionOffset > 0.5) {
                    viewPager.setCurrentItem(position + 1, true)
                } else {
                    viewPager.setCurrentItem(position, true)
                }
            }
        })
    }
}