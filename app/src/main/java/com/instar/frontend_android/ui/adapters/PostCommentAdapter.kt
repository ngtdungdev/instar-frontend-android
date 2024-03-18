package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.Post

class PostCommentAdapter(private val context: Context, private val data: MutableList<Post>, private val isEditImage: Boolean) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_post_comment_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        TODO("Not yet implemented")
    }

    override fun getItemCount(): Int = data.size

}