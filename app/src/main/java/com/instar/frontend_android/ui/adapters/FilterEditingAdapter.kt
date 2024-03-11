package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.net.Uri
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import java.io.File
import java.io.FileInputStream
import java.io.ObjectInputStream

class FilterEditingAdapter(private val context: Context, private val data: MutableList<ImageAndVideo>) : RecyclerView.Adapter<FilterEditingAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_post_filter_editing, parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if(item.type != ImageAndVideo.TYPE_IMAGE) {
            holder.imagePlay.visibility = View.GONE
            holder.videoView.setVideoURI(Uri.parse(item.uri))
            holder.videoView.start()
            holder.videoView.setOnCompletionListener {
                holder.videoView.start()
            }
        }else {
            try {
                holder.layout.visibility = View.GONE
                Glide.with(context)
                    .asBitmap()
                    .load(item.filePath)
                    .centerCrop()
                    .into(object : CustomTarget<Bitmap>() {
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            holder.image.setImageBitmap(resource)
                        }

                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.image)
        val layout: View = view.findViewById(R.id.video)
        val videoView: VideoView = layout.findViewById(R.id.videoView)
        val imagePlay: ImageView = layout.findViewById(R.id.imageplay)
    }
}