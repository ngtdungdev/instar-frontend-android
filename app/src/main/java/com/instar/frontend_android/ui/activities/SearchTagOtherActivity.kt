package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.instar.frontend_android.databinding.ActivitySearchTagOtherBinding
import com.instar.frontend_android.types.requests.PostRequest
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.launch

class SearchTagOtherActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySearchTagOtherBinding
    private lateinit var userService: UserService
    private lateinit var query: TextView
    private lateinit var userId: String

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

    }
}