package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.databinding.ActivityPostPreuploadingBinding

class PostPreUpLoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostPreuploadingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostPreuploadingBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}