package com.instar.frontend_android.ui.activities

import android.content.Context
import android.media.audiofx.LoudnessEnhancer
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityProfileBinding
import com.instar.frontend_android.databinding.ActivityStoryBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.StoryResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Images
import com.instar.frontend_android.ui.DTO.Story
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.StoryService
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryActivity: AppCompatActivity(), StoriesProgressView.StoriesListener {
    private lateinit var storyService: StoryService
    private lateinit var userService: UserService
    private lateinit var binding: ActivityStoryBinding
    private lateinit var tvName : TextView
    private lateinit var tvTime : TextView
    private lateinit var image : ImageView
    private lateinit var avatar : ImageView
    public var user: User? = null
    var id: String? = null

    private val PROGRESS_COUNT = 6
    private lateinit var storiesProgressView: StoriesProgressView
    private var resources: MutableList<Images> = mutableListOf()
    private var myStories: MutableList<Story> = mutableListOf()
    private var counter = 0

    private val durations = longArrayOf(
        500L, 1000L, 1500L, 4000L, 5000L, 1000
    )
    private var pressTime: Long = 0L
    private val limit = 500L


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_story)
        initView()

        id = intent.getSerializableExtra("user").toString()

        lifecycleScope.launch {
            try {
                val response = getUserData(id.toString())
                user = response.data?.user
                user?.let { updateUserInformation(it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        lifecycleScope.launch {
            try {
                val response = getStories()
                myStories = response.data?.myStories!!
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        storiesProgressView = findViewById(R.id.stories)
        storiesProgressView.setStoriesCount(PROGRESS_COUNT)
        storiesProgressView.setStoryDuration(3000L)
        // or
        storiesProgressView.setStoriesCountWithDurations(durations)
        storiesProgressView.setStoriesListener(this)
        counter = 0
        storiesProgressView.startStories(counter)

        image = findViewById(R.id.image)
//        image.setImageResource(resources[counter])

        // bind reverse view
        val reverse = findViewById<View>(R.id.reverse)
        reverse.setOnClickListener {
            storiesProgressView.reverse()
        }
        reverse.setOnTouchListener(onTouchListener)

        val skip = findViewById<View>(R.id.skip)
        skip.setOnClickListener {
            storiesProgressView.skip()
        }
        skip.setOnTouchListener(onTouchListener)
    }

    private fun updateUserInformation(user: User) {
        tvName.text = user.username

        Glide.with(this@StoryActivity)
            .load(user.profilePicture?.url)
            .placeholder(R.drawable.default_image) // Placeholder image
            .error(R.drawable.default_image) // Image to display if load fails
            .into(avatar)
    }

    private suspend fun getStories(): ApiResponse<StoryResponse> {
        return withContext(Dispatchers.IO) {
            storyService.getStoriesByUserId(id.toString()).awaitResponse()
        }
    }


    private val onTouchListener = View.OnTouchListener { view, event ->
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                pressTime = System.currentTimeMillis()
                storiesProgressView.pause()
                false
            }
            MotionEvent.ACTION_UP -> {
                val now = System.currentTimeMillis()
                storiesProgressView.resume()
                if (limit < now - pressTime) {
                    view.performClick()
                    true
                } else {
                    false
                }
            }
            else -> false
        }
    }

    private fun initView() {
        binding.apply {
            this@StoryActivity.tvName = tvName
            this@StoryActivity.tvTime = tvTime
            this@StoryActivity.image = image
        }

    }
    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    override fun onNext() {
//        image.setImageResource(resources[++counter])
    }

    override fun onPrev() {
        if ((counter - 1) < 0) return
//        image.setImageResource(resources[--counter])
    }

    override fun onComplete() {}

    override fun onDestroy() {
        storiesProgressView.destroy()
        super.onDestroy()
    }
}