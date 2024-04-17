package com.instar.frontend_android.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityDetailMyPostBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.PostAdapter
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.PostAdapterType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DetailMyPostActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailMyPostBinding

    private lateinit var btnBack: ImageButton
    private lateinit var recyclerView: RecyclerView

    private lateinit var userService: UserService;
    private lateinit var postAdapter: PostAdapter;

    private var postList: MutableList<PostAdapterType> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_detail_my_post)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        userService = ServiceBuilder.buildService(UserService::class.java, this)
        binding = ActivityDetailMyPostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        btnBack = binding.btnBack
        recyclerView = binding.recyclerViewNotification

        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager

        val userId: String? = intent.getStringExtra("userId")
        var user: User? = null;
        val fragmentManager = supportFragmentManager

        lifecycleScope.launch {
            val response = userId?.let { getUserData(userId) }
            if (response != null) {
                user = response.data?.user

                postAdapter = user?.let { PostAdapter(postList, lifecycleScope, it, fragmentManager) }!!;
                recyclerView.layoutManager = LinearLayoutManager(applicationContext)
                recyclerView.adapter = postAdapter
            };
        }

        btnBack.setOnClickListener {
            val intent = Intent(this, MainScreenActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }


        lifecycleScope.launch {
            val postDataList = userId?.let { getPosts(userId) }

            if (postDataList != null) {
                for (post in postDataList) {
                    postList.add(PostAdapterType(post, null, null, PostAdapterType.VIEW_TYPE_DEFAULT))
                }
            }
        }
    }

    private suspend fun getPosts(id: String): ArrayList<Post> {
        var postsList = ArrayList<Post>()
        val response = try {
            val response = userService.getTimelineMyPosts(id).awaitResponse()
            response
        } catch (error: Throwable) {
            // Handle error
            error.printStackTrace() // Print stack trace for debugging purposes
            null // Return null to indicate that an error occurred
        }
        if (response != null) {
            postsList = (response.data?.posts ?: ArrayList()) as ArrayList<Post>

        } else {
            // Handle the case where the response is null
            Log.e("Error", "Failed to get timeline posts")
        }

        return postsList
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}