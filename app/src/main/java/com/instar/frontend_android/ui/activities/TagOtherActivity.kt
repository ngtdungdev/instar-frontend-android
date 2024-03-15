package com.instar.frontend_android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.databinding.ActivityTagOthersBinding

class TagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTagOthersBinding
    private lateinit var btnCollaborator: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagOthersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnCollaborator = binding.btnCollaborator
        initView()
    }

    private fun initView() {
        btnCollaborator.setOnClickListener {
            val intent = Intent(this@TagOtherActivity, SearchTagOtherActivity::class.java)
            startActivity(intent)
        }
    }
}