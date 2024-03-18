package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter
import com.instar.frontend_android.ui.customviews.ViewEditText

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySearchTagOtherBinding
    private lateinit var message: EditText
    private lateinit var btnRemove: ImageButton

    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: PostTagAdapter
    private lateinit var userRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTagOtherBinding.inflate(layoutInflater)
        setContentView(binding.root)
        message = binding.message
        btnRemove = binding.iconDelete
        initView();
    }

    private fun initView() {
        val viewEditText = ViewEditText()
        viewEditText.EditTextTag(message,  btnRemove)
        viewEditText.setOnItemRemoveClick(object : ViewEditText.OnItemRemoveClick {
            override fun onFocusChange(view: View) {
                if (message.text.toString().isEmpty()) setMessage()
            }
        })
        message.setOnClickListener {

        }
//        userList = getUser()
//        userAdapter = PostTagAdapter(this ,userList)
//        userRecyclerView.adapter = userAdapter
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun setMessage() {
        message.hint = "Tìm kiếm người dùng"
    }


    private fun getUser(): MutableList<User> {
        val list: MutableList<User> = mutableListOf()
        return list
    }
}