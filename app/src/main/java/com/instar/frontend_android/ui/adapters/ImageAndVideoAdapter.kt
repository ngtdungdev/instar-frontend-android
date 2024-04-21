package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.content.Intent
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory
import com.instar.frontend_android.ui.activities.AddStoryActivity
import com.instar.frontend_android.ui.activities.ProfileActivity
import kotlin.math.ceil

class ImageAndVideoAdapter(
    private val context: Context,
    private val data: List<ImageAndVideoInternalMemory>,
    private val isListPost: Boolean,
    private var savePosition: Int
): RecyclerView.Adapter< ImageAndVideoAdapter.ImageViewHolder >(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.post_item_image, parent, false)
        return ImageViewHolder(view)
    }
    private var saveImage: ImageView? = null
    interface OnItemClickListener {
        fun onItemClick(position: Int)
        fun onDeleteClick(position: Int, savePosition: Int)
    }

    fun getListSelectorItem(): MutableList<Int> {
        return listSelectorItem
    }
    private var listener: OnItemClickListener? = null
    private var listSelectorItem: MutableList<Int> = mutableListOf()

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
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
        holder.image.alpha = 1F
        Glide.with(context)
            .load(item.filePath)
            .centerCrop()
            .into(holder.image)
        loadChecked(holder, position)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
            loadBtnChecked(holder,position)
        }
    }

    private fun loadBtnChecked(holder: ImageViewHolder, position: Int) {
        if(isListPost) {
            if(saveImage != null) {
                saveImage!!.alpha = 1F
            }
            if(listSelectorItem.contains(position) && holder.layout.isSelected) {
                if(savePosition != position) {
                    if(listener != null) {
                        listener!!.onItemClick(position)
                    }
                    holder.image.alpha = 0.7F
                    savePosition = position
                }
                else {
                    listSelectorItem.removeAt(listSelectorItem.indexOf(position))
                    holder.layout.isSelected = false
                    holder.number.visibility = View.GONE
                    holder.imageChecked.setBackgroundResource(R.drawable.background_unchecked)
                    holder.image.alpha = 1F
                    savePosition = listSelectorItem[listSelectorItem.size - 1]
                    if(listener != null) {
                        listener!!.onDeleteClick(position,savePosition)
                    }
                    notifyDataSetChanged()
                }
            }
            else if(listSelectorItem.size < 10) {
                listSelectorItem.add(position)
                if(listener != null) {
                    listener!!.onItemClick(position)
                }
                holder.number.visibility = View.VISIBLE
                holder.number.text = listSelectorItem.size.toString()
                holder.layout.isSelected = true
                holder.imageChecked.setBackgroundResource(R.drawable.background_checked)
                holder.image.alpha = 0.7F
                saveImage = holder.image
                savePosition = position
            }
        } else {
            if(listener != null) {
                listener!!.onItemClick(position)
            }
            if (saveImage != null) {
                saveImage!!.alpha = 1F
                saveImage!!.isSelected = false
            }
            holder.layout.isSelected = true
            holder.image.alpha = 0.7F
            saveImage = holder.image
        }
        listener?.onDeleteClick(position, savePosition)
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

    private fun loadChecked(holder: ImageViewHolder, position: Int) {
        if(isListPost) {
            if (position == savePosition) {
                if(!listSelectorItem.contains(position)) listSelectorItem.add(position)
                holder.number.visibility = View.VISIBLE
                holder.layout.isSelected = true
                holder.imageChecked.setBackgroundResource(R.drawable.background_checked)
                holder.number.text = listSelectorItem.size.toString()
                saveImage = holder.image
                holder.image.alpha = 0.7F
            } else if (listSelectorItem.contains(position) ) {
                holder.number.visibility = View.VISIBLE
                holder.layout.isSelected = true
                holder.image.alpha = 1F
                holder.imageChecked.setBackgroundResource(R.drawable.background_checked)
                holder.number.text = (listSelectorItem.indexOf(position) + 1).toString()
            }  else {
                holder.image.alpha = 1F
                holder.number.visibility = View.GONE
                holder.imageChecked.setBackgroundResource(R.drawable.background_unchecked)
            }
            holder.checked.visibility = View.VISIBLE
        }
        else {
            if (position != savePosition) {
                holder.image.alpha = 1F
            } else {
                holder.image.alpha = 0.7F
                saveImage = holder.image
            }
            holder.checked.visibility = View.GONE
        }
    }

    fun getSavePosition(): Int {
        return savePosition
    }

    class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val image: ImageView = view.findViewById(R.id.imageView)
        val time: TextView = view.findViewById(R.id.time)
        val layout: View = view.findViewById(R.id.layout)
        val checked: View = view.findViewById(R.id.checked)
        val imageChecked: ImageView = checked.findViewById(R.id.imageChecked)
        val number: TextView = checked.findViewById(R.id.number)
    }
}