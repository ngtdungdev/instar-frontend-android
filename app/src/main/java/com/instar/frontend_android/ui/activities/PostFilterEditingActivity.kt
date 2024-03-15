package com.instar.frontend_android.ui.activities

import android.content.Intent
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import android.os.Bundle
import android.widget.TextView
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.databinding.ActivityPostFilterEditingBinding
import com.instar.frontend_android.ui.adapters.HorizontalSpaceItemDecoration
import com.instar.frontend_android.ui.adapters.SelectedImageAdapter
import com.instar.frontend_android.ui.viewmodels.FilterEditingViewModel
import java.io.Serializable

class PostFilterEditingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPostFilterEditingBinding
    private lateinit var filterRecyclerView: RecyclerView
    private lateinit var filterEditingAdapter: SelectedImageAdapter
    private var imageAndVideo: MutableList<ImageAndVideo> = mutableListOf()
    private lateinit var viewModel: FilterEditingViewModel
    private lateinit var imageBack: ImageView
    private lateinit var btnContinue: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPostFilterEditingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        imageAndVideo = (intent.getSerializableExtra("Data") as? MutableList<ImageAndVideo>)!!
        viewModel = ViewModelProvider(this)[FilterEditingViewModel::class.java]
        filterRecyclerView = binding.recyclerView
        imageBack = binding.imageBack
        btnContinue = binding.btnContinue
        initView()
    }

    private fun initView() {
        imageBack.setOnClickListener {
            finish()
        }
        btnContinue.setOnClickListener {
            val intent = Intent(this@PostFilterEditingActivity, PostPreUpLoadingActivity::class.java).apply {
                putExtra("Data", imageAndVideo as Serializable)
            }
            startActivity(intent)
        }
        loadRecyclerView()
    }

    private fun loadRecyclerView() {
        filterEditingAdapter = SelectedImageAdapter(this, imageAndVideo, true)
        val snapHelper = PagerSnapHelper()
        snapHelper.attachToRecyclerView(filterRecyclerView)
        filterRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
            filterRecyclerView.layoutManager = layoutManager
            val scale = resources.displayMetrics.density
            val spacingInPixels = (10 * scale + 0.5f).toInt()
            filterRecyclerView.addItemDecoration(HorizontalSpaceItemDecoration(spacingInPixels))
        }
        filterRecyclerView.adapter = filterEditingAdapter
    }
    fun isVideoViewVisibleEnough(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, snapHelper: PagerSnapHelper): Boolean {
        val snapView = snapHelper.findSnapView(recyclerView.layoutManager) ?: return false
        val snapPosition = recyclerView.getChildAdapterPosition(snapView)
        val viewHolderPosition = viewHolder.adapterPosition
        return snapPosition == viewHolderPosition
    }
}