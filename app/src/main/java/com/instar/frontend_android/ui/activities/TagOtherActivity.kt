package com.instar.frontend_android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityTagOthersBinding
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter

class TagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivityTagOthersBinding
    private lateinit var btnCollaborator: TextView
    private lateinit var btnBack: ImageButton

    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: PostTagAdapter
    private lateinit var userRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTagOthersBinding.inflate(layoutInflater)
        setContentView(binding.root)
        btnCollaborator = binding.btnCollaborator
        btnBack = binding.imageBack
        initView()
    }

    private fun initView() {
        btnCollaborator.setOnClickListener {
            val intent = Intent(this@TagOtherActivity, SearchTagOtherActivity::class.java)
            startActivity(intent)
        }
//        userList = getUser()
//        userAdapter = PostTagAdapter(this ,userList)
//        userRecyclerView.adapter = userAdapter

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun getUser(): MutableList<User> {
        val list: MutableList<User> = mutableListOf()
        return list
    }
}