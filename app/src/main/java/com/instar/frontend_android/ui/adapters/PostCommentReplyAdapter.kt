package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Post

class PostCommentReplyAdapter(private val context: Context, private val data: MutableList<Post>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_post_comment_reply_item, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

    }

    override fun getItemCount(): Int = data.size

}