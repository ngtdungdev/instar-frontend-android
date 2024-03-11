package com.instar.frontend_android.ui.activities

import android.graphics.Rect
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import android.os.Bundle
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityPostFilterEditingBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.adapters.FilterEditingAdapter
import com.instar.frontend_android.ui.viewmodels.FilterEditingViewModel
import java.io.FileInputStream
import java.io.ObjectInputStream

class PostFilterEditingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostFilterEditingBinding
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterEditingAdapter: FilterEditingAdapter
    private var imageAndVideo: MutableList<ImageAndVideo>? = null
    private lateinit var viewModel: FilterEditingViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostFilterEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageAndVideo = intent.getSerializableExtra("Data") as? MutableList<ImageAndVideo>
        viewModel = ViewModelProvider(this)[FilterEditingViewModel::class.java]
        filterRecyclerView = binding.recyclerView
        initView()
    }

    private fun initView() {
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        filterEditingAdapter = FilterEditingAdapter(this, imageAndVideo!!)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(filterRecyclerView)
        filterRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                snapView?.let {
                    val snapPosition = recyclerView.getChildAdapterPosition(it)
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(snapPosition)
                    if (viewHolder is FilterEditingAdapter.ViewHolder && imageAndVideo!![snapPosition].type != ImageAndVideo.TYPE_IMAGE) {
                        if (isVideoViewVisibleEnough(recyclerView, viewHolder, snapHelper)) {
                            viewHolder.videoView.start()
                        } else {
                            viewHolder.videoView.pause()
                        }
                    }
                }
            }
        })
        filterRecyclerView.adapter = filterEditingAdapter
    }
    fun isVideoViewVisibleEnough(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, snapHelper: PagerSnapHelper): Boolean {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager) ?: return false
        val snapPosition = recyclerView.getChildAdapterPosition(snapView)
        val viewHolderPosition = viewHolder.adapterPosition
        return snapPosition == viewHolderPosition
    }

    fun isVideoViewVisibleEnough(videoView: VideoView): Boolean {
        val rect = Rect()
        val isVisible = videoView.getGlobalVisibleRect(rect)
        if (!isVisible) return false
        val displayedArea = rect.width() * rect.height()
        val totalArea = videoView.width * videoView.height
        val visiblePercentage = displayedArea * 100 / totalArea
        return visiblePercentage >= 50
    }

}