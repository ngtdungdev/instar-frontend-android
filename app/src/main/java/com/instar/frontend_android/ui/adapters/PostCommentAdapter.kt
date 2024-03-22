package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.ImageAndVideo
import com.instar.frontend_android.ui.DTO.Post
import org.w3c.dom.Text

class PostCommentAdapter(private val context: Context, private val data: MutableList<Comment>) : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val nameAndDay: TextView = view.findViewById(R.id.nameAndDay)
        val text: TextView = view.findViewById(R.id.text)
        val btnReply: TextView = view.findViewById(R.id.btnReply)
        val btnSeeMore: View = view.findViewById(R.id.btnSeeMore)
        val recyclerView: RecyclerView = view.findViewById(R.id.recyclerView)
        val like: ImageView = view.findViewById(R.id.btnLike)
        val textTotalLike: TextView = view.findViewById(R.id.textTotalLike)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_post_comment_item, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.nameAndDay.text = item.username
        holder.text.text = item.content
        holder.textTotalLike.text = item.likes.size.toString()
        holder.btnSeeMore.setOnClickListener {
            holder.recyclerView.visibility = View.VISIBLE
        }
        holder.btnReply.setOnClickListener {

        }
        holder.like.setOnClickListener {

        }
    }

    override fun getItemCount(): Int = data.size

}