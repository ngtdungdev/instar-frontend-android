package com.instar.frontend_android.ui.activities

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.Settings
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.instar.frontend_android.databinding.ActivityMainScreenBinding
import com.instar.frontend_android.ui.adapters.ScreenSlidePagerAdapter
import com.instar.frontend_android.ui.services.OnFragmentClickListener


class MainScreenActivity: AppCompatActivity(), OnFragmentClickListener{
    private lateinit var binding : ActivityMainScreenBinding
    private lateinit var viewPager : ViewPager2
    companion object {
        const val PERMISSION_CODE = 1001
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && Environment.isExternalStorageManager()) {
                initView()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (Environment.isExternalStorageManager()) {
                initView()
            } else {
                requestPermission()
            }
        } else {
            initView()
        }
    }
    private var listener: OnFragmentClickListener? = null
    @RequiresApi(Build.VERSION_CODES.R)
    private fun requestPermission() {
        try {
            val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
            intent.addCategory("android.intent.category.DEFAULT")
            intent.data = Uri.parse("package:$packageName")
            requestPermissionLauncher.launch(intent)
        } catch (e: Exception) {
            val intent = Intent()
            intent.action = Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION
            requestPermissionLauncher.launch(intent)
        }
    }

    private fun initView() {
        binding = ActivityMainScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewPager = binding.viewPager
        viewPager.adapter = ScreenSlidePagerAdapter(this@MainScreenActivity)
        viewPager.setCurrentItem(1, false)
        val recyclerView = viewPager.getChildAt(0) as RecyclerView
        val touchSlopField = RecyclerView::class.java.getDeclaredField("mTouchSlop")
        touchSlopField.isAccessible = true
        val touchSlop = touchSlopField.get(recyclerView) as Int
        touchSlopField.set(recyclerView, touchSlop * 2)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initView()
            } else {
                finish()
            }
        }
    }

    override fun onItemClick(position: Int, fragmentTag: String) {
        viewPager.setCurrentItem(position, true)
    }
}
