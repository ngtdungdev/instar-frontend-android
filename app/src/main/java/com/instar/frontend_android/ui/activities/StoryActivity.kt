package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityStoryBinding
import jp.shts.android.storiesprogressview.StoriesProgressView

class StoryActivity: AppCompatActivity(), StoriesProgressView.StoriesListener {
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


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_story)

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

    override fun onNext() {
        image.setImageResource(resources[++counter])
    }

    override fun onPrev() {
        if ((counter - 1) < 0) return
        image.setImageResource(resources[--counter])
    }

    override fun onComplete() {}

    override fun onDestroy() {
        // Very important!
        storiesProgressView.destroy()
        super.onDestroy()
    }
}