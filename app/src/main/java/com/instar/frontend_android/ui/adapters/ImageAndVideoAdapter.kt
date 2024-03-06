package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import java.text.SimpleDateFormat
import kotlin.math.ceil

class ImageAndVideoAdapter(private val context: Context, private val data: List<ImageAndVideoInternalMemory>): RecyclerView.Adapter< ImageAndVideoAdapter.ImageViewHolder >(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_image, parent, false)
        return ImageViewHolder(view)
    }
    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }
    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val item = data[position]
        if(item.type != MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE) {
            holder.time.visibility = View.VISIBLE
            holder.time.text = formatDuration(item.duration.toLong())
        } else  holder.time.visibility = View.GONE
        Glide.with(context)
            .load(item.filePath)
            .centerCrop()
            .into(holder.image)
    }
    private fun formatDuration(durationMillis: Long): String {
        if (durationMillis < 0) return ""
        val totalSeconds = ceil(durationMillis / 1000.0).toLong()
        val hours = totalSeconds / 3600
        val minutes = (totalSeconds % 3600) / 60
        val seconds = totalSeconds % 60
        return if (hours > 0) {
            String.format("%02d:%02d:%02d", hours, minutes, seconds)
        } else {
            String.format("%02d:%02d", minutes, seconds)
        }
    }
    override fun getItemCount(): Int {return data.size}

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val time: TextView = view.findViewById(R.id.time)
    }
}