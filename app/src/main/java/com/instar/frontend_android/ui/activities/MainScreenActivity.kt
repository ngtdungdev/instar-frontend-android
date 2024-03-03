package com.instar.frontend_android.ui.activities
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityMainScreenBinding
import com.instar.frontend_android.ui.adapters.ScreenSlidePagerAdapter
import com.instar.frontend_android.ui.adapters.SlowScroller
import com.instar.frontend_android.ui.fragments.HomeFragment
import java.lang.Math.abs

class MainScreenActivity : AppCompatActivity(), HomeFragment.OnItemClickListener {
    private lateinit var biding : ActivityMainScreenBinding
    private lateinit var viewPager : ViewPager2
    private var savePosition: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        biding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(biding.root)
        viewPager = biding.viewPager
        viewPager.adapter = ScreenSlidePagerAdapter(this@MainScreenActivity)
        viewPager.setCurrentItem(1, false)
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)
//        viewPager.setPageTransformer { page, position ->
//            page.translationX = page.width * -position
//            page.alpha = 1 - kotlin.math.abs(position)
//        }

//        viewPager.setPageTransformer { page, position ->
//            val normalizedPosition = kotlin.math.abs(position)
//            page.translationX = page.width * -position
//            page.alpha = 1 - normalizedPosition
//            val scaleFactor = 0.85f + (1 - kotlin.math.abs(position)) * 0.15f
//            page.scaleX = scaleFactor
//            page.scaleY = scaleFactor
//        }

//        val recyclerView1 = viewPager.getChildAt(0) as RecyclerView
//        val layoutManager = recyclerView1.layoutManager as LinearLayoutManager
//
//        try {
//            val scrollerField = layoutManager.javaClass.getDeclaredField("mScroller")
//            scrollerField.isAccessible = true
//            scrollerField.set(layoutManager, SlowScroller(viewPager.context))
//        } catch (e: NoSuchFieldException) {
//            e.printStackTrace()
//        } catch (e: IllegalAccessException) {
//            e.printStackTrace()
//        }
    }

    override fun onItemClick(position: Int?) {
        if (position != null) {
            savePosition = position
            viewPager.setCurrentItem(position, true)
        }
    }


}