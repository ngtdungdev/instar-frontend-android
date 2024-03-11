package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideoInternalMemory

class ImageAndVideoFilterAdapter(
    private val context: Context,
    private val data: List<ImageAndVideoInternalMemory>,
): RecyclerView.Adapter< RecyclerView.ViewHolder >(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageAndVideoAdapter.ImageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_post_filter_editing, parent, false)
        return ImageAndVideoAdapter.ImageViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }
}