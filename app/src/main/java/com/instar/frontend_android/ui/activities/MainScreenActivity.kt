package com.instar.frontend_android.ui.activities
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
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
    companion object {
        const val PERMISSION_CODE = 1001
    }
    @RequiresApi(Build.VERSION_CODES.R)
    private val startActivityResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && Environment.isExternalStorageManager()) {
            initView()
        } else {
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R && !Environment.isExternalStorageManager()) {
            val intent = Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION)
            val uri = Uri.fromParts("package", packageName, null)
            intent.data = uri
            startActivityResult.launch(intent)
        } else {
            initView()
        }
    }

    private fun initView() {
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
//
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            } else {
                finish()
            }
        }
    }
    override fun onItemClick(position: Int?) {
        if (position != null) {
            savePosition = position
            viewPager.setCurrentItem(position, true)
        }
    }


}