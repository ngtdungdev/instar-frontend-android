package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostTagAdapter
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class SearchTagOtherActivity: AppCompatActivity() {
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
        setContentView(binding.root)

        val sharedPreferences = applicationContext.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)

        val accessToken = sharedPreferences.getString("accessToken", null)

        if (accessToken != null) {
            val decodedTokenJson = Helpers.decodeJwt(accessToken)
            userId = decodedTokenJson.getString("id")
        }

        initView()
    }

    fun search() {
        lifecycleScope.launch {
            val response = try {
                val response = userService.searchFollowingUser(query.text.toString()).awaitResponse()
                response
            } catch (error: Throwable) {
                // Handle error
                error.printStackTrace() // Print stack trace for debugging purposes
                null // Return null to indicate that an error occurred
            }

            if (response != null && response.status == "200") {
                Toast.makeText(applicationContext, "Post created", Toast.LENGTH_LONG).show()
                finish()
            } else {
                // Handle the case where the response is null
                Log.e("Error", "Failed to get timeline posts")
            }
        }
    }

    fun initView() {
        query.setOnClickListener {

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