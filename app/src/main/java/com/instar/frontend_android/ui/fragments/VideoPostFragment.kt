package com.instar.frontend_android.ui.fragments

import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.Surface
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.instar.frontend_android.databinding.FragmentPostVideoBinding
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.adapters.ImageAndVideoAdapter

class VideoPostFragment : Fragment() {
    private lateinit var binding: FragmentPostVideoBinding
    private lateinit var newData: ImageAndVideoInternalMemory
    private lateinit var videoView: VideoView
    private lateinit var imageView: ImageView
    private lateinit var layout: View

    fun updateData(newData: ImageAndVideoInternalMemory) {
        this.newData = newData
        if(this::videoView.isInitialized) {
            imageView.visibility = View.GONE
            videoView.setVideoURI(Uri.parse(newData.uri))
            videoView.start()
        }
    }
    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (hidden) {
            videoView.pause()
            imageView.visibility = View.VISIBLE
        } else {
            videoView.start()
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        binding = FragmentPostVideoBinding.inflate(inflater, container, false)
        videoView = binding.videoView
        imageView = binding.imageplay
        layout = binding.layout
        initView()
        return binding.root
    }
    private fun initView() {
        videoView.setVideoURI(Uri.parse(newData.uri))
        videoView.start()
        videoView.setOnPreparedListener {
            imageView.visibility = View.GONE
        }
        layout.setOnClickListener {
            if (videoView.isPlaying) {
                imageView.visibility = View.VISIBLE
                videoView.pause()
            } else {
                imageView.visibility = View.GONE
                videoView.start()
            }
        }
        videoView.setOnCompletionListener {
            videoView.start()
        }
    }
}