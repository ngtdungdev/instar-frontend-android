package com.instar.frontend_android.ui.activities

import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityProfileBinding
import com.instar.frontend_android.databinding.ActivityStoryBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import jp.shts.android.storiesprogressview.StoriesProgressView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class StoryActivity: AppCompatActivity(), StoriesProgressView.StoriesListener {
    private lateinit var userService: UserService
    private lateinit var binding: ActivityStoryBinding
    private lateinit var tvName : TextView
    private lateinit var tvTime : TextView
//    private lateinit var image : ImageView
    private lateinit var avatar : ImageView
    public var user: User? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStoryBinding.inflate(layoutInflater)
        setContentView(R.layout.activity_story)
        initView()

        storiesProgressView = findViewById(R.id.stories)
        storiesProgressView.setStoriesCount(PROGRESS_COUNT)
        storiesProgressView.setStoryDuration(3000L)
        // or
        // storiesProgressView.setStoriesCountWithDurations(durations)
        storiesProgressView.setStoriesListener(this)
        counter = 0
        storiesProgressView.startStories(counter)

        image = findViewById(R.id.image)
        image.setImageResource(resources[counter])

        // bind reverse view
        val reverse = findViewById<View>(R.id.reverse)
        reverse.setOnClickListener {
            storiesProgressView.reverse()
        }
        reverse.setOnTouchListener(onTouchListener)

        // bind skip view
        val skip = findViewById<View>(R.id.skip)
        skip.setOnClickListener {
            storiesProgressView.skip()
        }
        skip.setOnTouchListener(onTouchListener)
    }

        private val PROGRESS_COUNT = 6
    private lateinit var storiesProgressView: StoriesProgressView
    private lateinit var image: ImageView
    private var counter = 0
    private val resources = intArrayOf(
        R.mipmap.no1,
        R.mipmap.no2,
        R.mipmap.no3,
        R.mipmap.no1,
        R.mipmap.no2,
        R.mipmap.no3
    )
    private val durations = longArrayOf(
        500L, 1000L, 1500L, 4000L, 5000L, 1000
    )
    private var pressTime: Long = 0L
    private val limit = 500L

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
//            this@StoryActivity.avatar = avatar
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
//        if ((counter - 1) < 0) return
//        image.setImageResource(resources[--counter])
    }

    override fun onComplete() {}

    override fun onDestroy() {
        // Very important!
//        storiesProgressView.destroy()
        super.onDestroy()
    }
}