package com.instar.frontend_android.ui.activities

import com.instar.frontend_android.ui.DTO.ImageAndVideo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
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
        filterRecyclerView.adapter = filterEditingAdapter
    }

}