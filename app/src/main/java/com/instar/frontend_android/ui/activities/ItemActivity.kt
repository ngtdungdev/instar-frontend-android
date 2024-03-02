package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R

class ItemActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recycler_view_item_newsfeed);

    }
}