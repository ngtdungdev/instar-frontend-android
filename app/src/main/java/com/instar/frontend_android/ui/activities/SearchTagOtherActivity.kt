package com.instar.frontend_android.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySearchTagOtherBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTagOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }
}