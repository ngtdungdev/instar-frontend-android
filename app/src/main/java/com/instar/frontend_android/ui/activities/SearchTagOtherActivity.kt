package com.instar.frontend_android.ui.activities

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter
import com.instar.frontend_android.ui.customviews.ViewEditText

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var btnRemove: ImageButton
    private lateinit var binding: ActivitySearchTagOtherBinding
    private lateinit var userService: UserService
    private lateinit var query: EditText
    private lateinit var userId: String

    private lateinit var userList: MutableList<User>
    private lateinit var userAdapter: PostTagAdapter
    private lateinit var userRecyclerView: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySearchTagOtherBinding.inflate(layoutInflater)
        userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
        query = binding.message;
        userRecyclerView = binding.searchTagRV
        setContentView(binding.root)
        btnRemove = binding.iconDelete
        initView();
    }

    fun setQuery() {
        query.hint = "Tìm kiếm người dùng"
    }


    fun getUser(): MutableList<User> {
        val list: MutableList<User> = mutableListOf()
        return list
    }

    fun initView() {
        val debounceHandler = Handler(Looper.getMainLooper())
        var debounceRunnable: Runnable? = null

        query.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                debounceRunnable?.let {
                    debounceHandler.removeCallbacks(it)
                }
                debounceRunnable = Runnable {
                    // Your logic to execute after debounce duration
                    if (s.toString().isEmpty()) {
                        setQuery()
                    } else {
                        search();
                    }
                }
                debounceHandler.postDelayed(debounceRunnable!!, 500)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not used
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Not used
            }
        })

        query.setOnClickListener {
        }




    }

    fun search() {
        lifecycleScope.launch {
            val response = try {
                val response =
                    userService.searchFollowingUser(query.text.toString()).awaitResponse()
                response
            } catch (error: Throwable) {
                // Handle error
                error.printStackTrace() // Print stack trace for debugging purposes
                null // Return null to indicate that an error occurred
            }

            if (response != null && response.data.followingUsers != null) {
                Log.d("users", response.data.followingUsers.toString())
                userList = response.data.followingUsers as MutableList<User>;
                userList.forEach { user ->
                    Log.d("User", user.username)
                }

                updateUserList();
            } else {
                // Handle the case where the response is null
                Log.e("Error", "Failed to get timeline posts")
            }
        }
    }

    fun updateUserList() {
        userAdapter = PostTagAdapter(this, userList)
        userRecyclerView.adapter = userAdapter
    }
}
