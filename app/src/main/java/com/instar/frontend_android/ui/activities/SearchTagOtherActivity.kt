package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySearchTagOtherBinding
    private lateinit var message: EditText

    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: PostTagAdapter
    private lateinit var userRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTagOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        message = binding.message
        initView();
    }

    private fun initView() {
        message.setOnClickListener {

        }
        userList = getUser()
        userAdapter = PostTagAdapter(this ,userList)
        userRecyclerView.adapter = userAdapter
    }

    private fun getUser(): MutableList<User> {
        val list: MutableList<User> = mutableListOf()
        return list
    }
}