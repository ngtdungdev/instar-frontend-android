package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityStoryBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.StoryResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Story
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.StoryService
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryActivity: AppCompatActivity(), StoriesProgressView.StoriesListener {
    private lateinit var storyService: StoryService
    private lateinit var userService: UserService
    private lateinit var binding: ActivityStoryBinding
    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var tvName : TextView
    private lateinit var tvTime : TextView
    private lateinit var image : ImageView
    private lateinit var imageBack : ImageView
    private lateinit var avatar : ImageView
    private lateinit var reverse : View
    private lateinit var skip : View
    private var myStories: MutableList<Story> = mutableListOf()
    var user: User? = null
    var id: String? = null

    private var counter = 0
    private var pressTime: Long = 0L
    private val limit = 500L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userService = ServiceBuilder.buildService(UserService::class.java, this)
        storyService = ServiceBuilder.buildService(StoryService::class.java, this)
        initView()

        id = intent.getStringExtra("user")

        lifecycleScope.launch {
            try {
                val response = getUserData(id!!)
                user = response.data?.user
                updateUserInformation(user!!)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        lifecycleScope.launch {
            try {
                val response = getStories()
                myStories = response.data?.myStories!!
                storiesProgressView.setStoriesCount(myStories.size)
                storiesProgressView.setStoryDuration(16000L)
                storiesProgressView.setStoriesListener(this@StoryActivity)

                // Bắt đầu từ phần tử đầu tiên
                tvTime.text = Helpers.convertToTimeAgo(myStories[counter].createdAt)
                loadImage(myStories[counter].fileUploads.url.toString())

                storiesProgressView.startStories(counter)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUserInformation(user: User) {
        tvName.text = user.username
        Glide.with(this@StoryActivity)
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(avatar)
    }

    private suspend fun getStories(): ApiResponse<StoryResponse> {
        return withContext(Dispatchers.IO) {
            storyService.getStoriesByUserId(id!!).awaitResponse()
        }
    }

    private fun initView() {
        binding.apply {
            this@StoryActivity.tvName = tvName
            this@StoryActivity.tvTime = tvTime
            this@StoryActivity.avatar = avatar
            this@StoryActivity.image = image
            this@StoryActivity.imageBack = imageBack
            this@StoryActivity.reverse = reverse
            this@StoryActivity.skip = skip
            this@StoryActivity.storiesProgressView = stories
        }
        reverse.setOnClickListener {
            this.reverse()
        }

        skip.setOnClickListener {
            this.skip()
        }

        imageBack.setOnClickListener {
            finish()
        }
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    override fun onNext() {
        if (counter < myStories.size - 1) {
            counter++
            tvTime.text = Helpers.convertToTimeAgo(myStories[counter].createdAt)
            loadImage(myStories[counter].fileUploads.url.toString())
            storiesProgressView.skip() // Chuyển slide tới slide tiếp theo
        }
    }

    override fun onPrev() {
        if (counter > 0) {
            counter--
            tvTime.text = Helpers.convertToTimeAgo(myStories[counter].createdAt)
            loadImage(myStories[counter].fileUploads.url.toString())
            storiesProgressView.reverse() // Chuyển slide tới slide tiếp theo
        }
    }

    private fun reverse() {
        if (counter > 0){
            counter--
            tvTime.text = Helpers.convertToTimeAgo(myStories[counter].createdAt)
            Toast.makeText(this, myStories[counter].fileUploads.url.toString(), Toast.LENGTH_SHORT).show()
            loadImage(myStories[counter].fileUploads.url.toString())
            storiesProgressView.reverse() // Chuyển slide về slide trước đó
        }
        else return
    }

    private fun skip() {
        if (counter < myStories.size -1) {
            counter++
            tvTime.text = Helpers.convertToTimeAgo(myStories[counter].createdAt)
            Toast.makeText(this, myStories[counter].fileUploads.url.toString(), Toast.LENGTH_SHORT).show()
            loadImage(myStories[counter].fileUploads.url.toString())
            storiesProgressView.skip()
        }
        else return
    }

    private fun loadImage(imageUrl: String) {
        Glide.with(this@StoryActivity)
            .load(imageUrl)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(image)
    }

    override fun onComplete() {}

    override fun onDestroy() {
        storiesProgressView.destroy()
        super.onDestroy()
    }
}