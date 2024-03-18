package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.graphics.Point
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.Messages
import com.instar.frontend_android.ui.viewmodels.SaveAndReturnImageToFile

class SelectedImageAdapter(private val context: Context, private val data: MutableList<ImageAndVideo>, private val isEditImage: Boolean) : RecyclerView.Adapter<SelectedImageAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = when (isEditImage) {
            true -> LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_post_filter_editing, parent, false)
            false -> LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_post_pre_up_loading, parent, false)
        }
        if(data.size == 1) {
            val layoutParams = RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            view.layoutParams = layoutParams
        }
        return ViewHolder(view)
    }

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        if(item.type != ImageAndVideo.TYPE_IMAGE) {
            holder.imagePlay.visibility = View.GONE
            holder.videoView.setVideoURI(Uri.parse(item.uri))
            holder.videoView.setOnPreparedListener { mediaPlayer ->
                val videoWidth = mediaPlayer.videoWidth
                val videoHeight = mediaPlayer.videoHeight
                val videoProportion = videoWidth.toFloat() / videoHeight.toFloat()
                val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
                val display = windowManager.defaultDisplay
                val size = Point()
                display.getSize(size)
                val screenWidth = size.x
                val screenHeight = size.y
                val screenProportion = screenWidth.toFloat() / screenHeight.toFloat()
                val layoutParams = holder.videoView.layoutParams
                if (videoProportion > screenProportion) {
                    layoutParams.width = screenWidth
                    layoutParams.height = (screenWidth.toFloat() / videoProportion).toInt()
                } else {
                    layoutParams.width = (videoProportion * screenHeight.toFloat()).toInt()
                    layoutParams.height = screenHeight
                }
                holder.videoView.layoutParams = layoutParams
            }

            holder.videoView.setOnCompletionListener {
                holder.videoView.start()
            }
        }else {
            try {
                Glide.with(context)
                    .load(SaveAndReturnImageToFile.stringToBitmap(item.filePath, context))
                    .centerCrop()
                    .into(holder.image)
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
