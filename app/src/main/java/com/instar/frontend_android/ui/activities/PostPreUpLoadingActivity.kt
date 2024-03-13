package com.instar.frontend_android.ui.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityPostPreupLoadingBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.adapters.HorizontalSpaceItemDecoration
import com.instar.frontend_android.ui.adapters.SelectedImageAdapter

class PostPreUpLoadingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostPreupLoadingBinding
    private lateinit var preUpLoadingAdapter: SelectedImageAdapter
    private var imageAndVideo: MutableList<ImageAndVideo> = mutableListOf()
    private lateinit var preUpLoadingRecyclerView: RecyclerView
    private lateinit var btnTagOther : View
    private lateinit var imageBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostPreupLoadingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageBack = binding.imageBack
        preUpLoadingRecyclerView = binding.recyclerView
        btnTagOther = binding.btnTagOthers
        imageAndVideo = (intent.getSerializableExtra("Data") as? MutableList<ImageAndVideo>)!!
        initView()
    }

    private fun initView() {
        imageBack.setOnClickListener {
            finish()
        }
        btnTagOther.setOnClickListener {
            val intent = Intent(this@PostPreUpLoadingActivity, TagOtherActivity::class.java)
            startActivity(intent)
        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        preUpLoadingAdapter = SelectedImageAdapter(this, imageAndVideo, false)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(preUpLoadingRecyclerView)
        preUpLoadingRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val snapView = snapHelper.findSnapView(recyclerView.layoutManager)
                snapView?.let {
                    val snapPosition = recyclerView.getChildAdapterPosition(it)
                    val viewHolder = recyclerView.findViewHolderForAdapterPosition(snapPosition)
                    if (viewHolder is SelectedImageAdapter.ViewHolder && imageAndVideo[snapPosition].type != ImageAndVideo.TYPE_IMAGE) {
                        if (isVideoViewVisibleEnough(recyclerView, viewHolder, snapHelper)) {
                            viewHolder.videoView.start()
                        } else {
                            viewHolder.videoView.pause()
                        }
                    }
                }
            }
        })
        if(imageAndVideo.size > 1) {
            val layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
            preUpLoadingRecyclerView.layoutManager = layoutManager
            val scale = resources.displayMetrics.density
            val spacingInPixels = (10 * scale + 0.5f).toInt()
            preUpLoadingRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(spacingInPixels))
        }
        preUpLoadingRecyclerView.adapter = preUpLoadingAdapter
    }

    fun isVideoViewVisibleEnough(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, snapHelper: PagerSnapHelper): Boolean {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager) ?: return false
        val snapPosition = recyclerView.getChildAdapterPosition(snapView)
        val viewHolderPosition = viewHolder.adapterPosition
        return snapPosition == viewHolderPosition
    }
}