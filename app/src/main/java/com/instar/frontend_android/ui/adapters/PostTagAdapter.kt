package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.enum.EnumUtils
import com.instar.frontend_android.interfaces.InterfaceUtils
import com.instar.frontend_android.ui.DTO.User



class PostTagAdapter(private val context: Context, private val data: MutableList<User>, private val postTagType: EnumUtils.PostTagType, private val itemClickListener: InterfaceUtils.OnItemClickListener) : RecyclerView.Adapter<PostTagAdapter.PostTagHolder>() {
    class PostTagHolder(view: View, context: Context) : RecyclerView.ViewHolder(view) {
        val avatar: ImageView = view.findViewById(R.id.avatar)
        val title: TextView = view.findViewById(R.id.title)
        val text: TextView = view.findViewById(R.id.text)
        val button: ImageView = view.findViewById(R.id.button)
        val context: Context = context;

        @SuppressLint("SetTextI18n", "ClickableViewAccessibility")
        fun bind(user: User) {
            title.text = user.fullname

            Glide.with(context)
                .load(user.profilePicture?.url)
                .placeholder(R.drawable.default_image) // Placeholder image
                .error(R.drawable.default_image) // Image to display if load fails
                .into(avatar)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostTagHolder {
        return PostTagHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.adapter_user, parent, false), context)
    }

    override fun onBindViewHolder(holder: PostTagHolder, position: Int) {
        val item = data[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            itemClickListener.onItemClick(item)
        }


        holder.button.setOnClickListener {
            itemClickListener.onItemCloseClick(item)
        }

        if (postTagType == EnumUtils.PostTagType.TAG) {
            holder.button.setBackgroundResource(R.drawable.baseline_close_24)
            holder.button.visibility = View.VISIBLE
        } else if (postTagType == EnumUtils.PostTagType.TAG_SEARCH) {
            holder.button.visibility = View.GONE
        } else {
            holder.button.setBackgroundResource(R.drawable.icon_message_camera)
        }
    }

    override fun getItemCount(): Int = data.size
}
