package com.instar.frontend_android.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.instar.frontend_android.R

class ProfileActivity(private val mData: List<String>) : AppCompatActivity()  {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bottom_sheet_layout)
    }

}