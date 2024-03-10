package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Messages
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter.FriendAvatar
import com.instar.frontend_android.ui.adapters.NewsFollowAdapter.PersonalAvatar

class DirectMessageAdapter(private val data: List<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemAvatar: View

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            Messages.TYPE_AVATAR -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_avatar_item, parent, false)
            Messages.TYPE_SENT_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_right_item, parent, false)
            Messages.TYPE_RECEIVED_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_left_item, parent, false)
            else -> null
        }
        return when (viewType) {
            Messages.TYPE_AVATAR -> AvatarViewHolder(view!!)
            Messages.TYPE_SENT_MESSAGE -> SentMessageViewHolder(view!!)
            Messages.TYPE_RECEIVED_MESSAGE -> ReceivedMessageViewHolder(view!!)
            else -> throw IllegalArgumentException("Error")
        }
    }
    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Messages = data[position]
        when (holder) {
            is AvatarViewHolder -> {
                bindAvatar(holder, item)
            }

            is SentMessageViewHolder -> {
                bindSentMessage(holder, item)
            }

            is ReceivedMessageViewHolder -> {
                holder.imageAvatar.visibility = if (shouldShowAvatar(position)) View.VISIBLE else View.INVISIBLE
                bindReceivedMessage(holder, item)
            }
        }
    }

    private fun shouldShowAvatar(position: Int): Boolean {
        if (position == data.size - 1) return true
        return data[position].type != data[position + 1].type
    }

    private fun bindAvatar(holder: AvatarViewHolder, item: Messages) {
//        holder.imageButton.setBackgroundResource(item.imgPath)
    }
    private fun bindSentMessage(holder: SentMessageViewHolder, item: Messages) {
//        holder.imageButton.setBackgroundResource(item.imgPath)
    }

    private fun bindReceivedMessage(holder: ReceivedMessageViewHolder, item: Messages) {
    }
    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageAvatar: View = itemView.findViewById(R.id.imageAvatar)
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imageButton: ImageButton = itemView.findViewById(R.id.imageButton) vd
    }

    override fun getItemCount(): Int = data.size
}