package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.instar.frontend_android.R

class EditAddStoryActivity : AppCompatActivity() {
    private lateinit var imageStory: ImageView
    private lateinit var imgBack: ImageButton
    private lateinit var imgNext: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_edit_add_story)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        imageStory = findViewById(R.id.imageStory)
        imgBack = findViewById(R.id.imgBack)
        imgNext = findViewById(R.id.imgNext)

        val imageUri = intent.getStringExtra("imageUri")
        Glide.with(this).load(imageUri).into(imageStory)

        imgBack.setOnClickListener {
            onBackPressed() // Kích hoạt hành động trở về Activity trước đó
        }
        imgNext.setOnClickListener {
            // xử lý để đăng story
        }
    }
}

