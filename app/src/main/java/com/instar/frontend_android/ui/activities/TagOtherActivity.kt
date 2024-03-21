package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityTagOthersBinding
import com.instar.frontend_android.enum.EnumUtils
import com.instar.frontend_android.interfaces.InterfaceUtils
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter

class TagOtherActivity: AppCompatActivity() {
    private val SEARCH_TAG_OTHER_REQUEST_CODE = 101

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

        userList = intent.getSerializableExtra("userList") as? ArrayList<User> ?: mutableListOf()
        userRecyclerView = binding.tagRV

        userRecyclerView.layoutManager = LinearLayoutManager(this)

        initView()
        updateUserList()

    }

    private fun initView() {
        btnCollaborator.setOnClickListener {
            val intent = Intent(this@TagOtherActivity, SearchTagOtherActivity::class.java)
            intent.putExtra("userList", ArrayList(userList)) // Chuyển userList qua Intent
            startActivityForResult(intent, SEARCH_TAG_OTHER_REQUEST_CODE) // Bắt đầu activity với mã yêu cầu
        }

        userAdapter = PostTagAdapter(this, userList, EnumUtils.PostTagType.TAG, object : InterfaceUtils.OnItemClickListener {
            override fun onItemClick(user: User) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemCloseClick(user: User) {
                // Kiểm tra xem user có trong tagList hay không
                userList = userList.filter { it.id != user.id }.toMutableList()

                updateUserList()
            }
        })

        userRecyclerView.adapter = userAdapter

        btnBack.setOnClickListener {
            val intent = Intent()
            intent.putExtra("tagList", ArrayList(userList)) // Chuyển tagList qua Intent
            setResult(Activity.RESULT_OK, intent)
            finish() // Kết thúc activity và trả về kết quả
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateUserList() {
        userAdapter = PostTagAdapter(this, userList, EnumUtils.PostTagType.TAG, object : InterfaceUtils.OnItemClickListener {
            override fun onItemClick(user: User) {

            }

            @SuppressLint("NotifyDataSetChanged")
            override fun onItemCloseClick(user: User) {
                // Kiểm tra xem user có trong tagList hay không
                userList = userList.filter { it.id != user.id }.toMutableList()

                updateUserList()
            }
        })

        userRecyclerView.adapter = userAdapter
        userAdapter.notifyDataSetChanged()
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == SEARCH_TAG_OTHER_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                val tagList = data?.getSerializableExtra("tagList") as? ArrayList<User>
                if (tagList != null) {
                    userList = tagList
                    updateUserList()
                }
            }
        }
    }
}