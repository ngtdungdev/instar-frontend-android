package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioManager
import android.net.Uri
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R

class CarouselAdapter(
    private val mediaList: List<Pair<String, MediaType>>,
    private val gestureDetector: GestureDetector? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recyclerView: RecyclerView? = null
    private var centerPosition: Int = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MediaType.IMAGE.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel, parent, false)
                ImageViewHolder(view)
            }
            MediaType.VIDEO.ordinal -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.item_carousel_video, parent, false)
                VideoViewHolder(view, parent.context)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val (mediaUrl, mediaType) = mediaList[position]
        when (mediaType) {
            MediaType.IMAGE -> (holder as ImageViewHolder).bind(mediaUrl)
            MediaType.VIDEO -> (holder as VideoViewHolder).bind(mediaUrl, position == centerPosition)
        }
    }

    override fun getItemCount(): Int {
        return mediaList.size
    }

    override fun getItemViewType(position: Int): Int {
        return mediaList[position].second.ordinal
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
        recyclerView.addOnScrollListener(scrollListener)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
        recyclerView.removeOnScrollListener(scrollListener)
    }

    private val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)
            updateCenterPosition()
        }
    }

    private fun updateCenterPosition() {
        val layoutManager = recyclerView?.layoutManager
        if (layoutManager is LinearLayoutManager) {
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition()
            centerPosition = (firstVisibleItemPosition + lastVisibleItemPosition) / 2
            notifyItemChanged(centerPosition)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.imageView)

        init {
            gestureDetector?.let { gd ->
                imageView.setOnTouchListener { _, event ->
                    gd.onTouchEvent(event)
                    true
                }
            }
        }

        fun bind(imageUrl: String) {
            Glide.with(itemView.context)
                .load(imageUrl)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(imageView)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    inner class VideoViewHolder(itemView: View, private val context: Context) : RecyclerView.ViewHolder(itemView) {
        private val volumeButton: ImageButton = itemView.findViewById(R.id.volumeButton)
        private val videoView: VideoView = itemView.findViewById(R.id.videoView)
        private var isVideoPlaying: Boolean = false
        private var videoUrl: String? = null
        private var audioManager: AudioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        init {
            gestureDetector?.let { gd ->
                videoView.setOnTouchListener { _, event ->
                    gd.onTouchEvent(event)
                    true
                }
            }

            videoView.setOnClickListener {
                if (isVideoPlaying) {
                    pauseVideo()
                } else {
                    startVideo()
                }
            }

            volumeButton.setOnClickListener {
                toggleVolume()
            }

            videoView.setOnCompletionListener {
                // Video has ended, reset playback position to the beginning
                videoView.seekTo(0)
                isVideoPlaying = false
            }
        }

        fun bind(videoUrl: String, isCenterPosition: Boolean) {
            this.videoUrl = videoUrl
            if (isCenterPosition) {
                startVideo()
            } else {
                pauseVideo()
            }
        }

        private fun startVideo() {
            videoView.setVideoURI(Uri.parse(videoUrl))
            videoView.setOnPreparedListener {
                videoView.start()
                isVideoPlaying = true
            }
        }

        private fun pauseVideo() {
            videoView.pause()
            isVideoPlaying = false
        }

        private fun toggleVolume() {
            try {
                val currentVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC)
                val maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
                if (currentVolume == 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0)
                    volumeButton.setImageResource(R.drawable.ic_volume_up)
                } else {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
                    volumeButton.setImageResource(R.drawable.ic_volume_off)
                }
            } catch (e: Exception) {
                Log.e("VideoPlayer", "Error toggling volume: ${e.message}")
            }
        }
    }

    enum class MediaType {
        IMAGE,
        VIDEO
    }
}
