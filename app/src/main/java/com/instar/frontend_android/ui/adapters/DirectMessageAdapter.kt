package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.services.MessageService

class DirectMessageAdapter(private val data: List<Message>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemAvatar: View
    private lateinit var messageService: MessageService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            Message.TYPE_AVATAR -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_avatar_item, parent, false)
            Message.TYPE_SENT_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_right_item, parent, false)
            Message.TYPE_RECEIVED_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_left_item, parent, false)
            else -> null
        }
        return when (viewType) {
            Message.TYPE_AVATAR -> AvatarViewHolder(view!!)
            Message.TYPE_SENT_MESSAGE -> SentMessageViewHolder(view!!)
            Message.TYPE_RECEIVED_MESSAGE -> ReceivedMessageViewHolder(view!!)
            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun getItemViewType(position: Int): Int {
        return data[position].type
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Message = data[position]
        when (holder) {
            is AvatarViewHolder -> {
                bindAvatar(holder, item)
            }

            is ReceivedMessageViewHolder -> {
                holder.senderAvatar.visibility = if (shouldShowAvatar(position)) View.VISIBLE else View.INVISIBLE
                bindReceivedMessage(holder, item)
            }

            is SentMessageViewHolder -> {
                bindSentMessage(holder, item)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun shouldShowAvatar(position: Int): Boolean {
        if (position == data.size - 1) return true
        return data[position].type != data[position + 1].type
    }

    private fun bindAvatar(holder: AvatarViewHolder, item: Message) {
//        holder.imageButton.setBackgroundResource(item.imgPath)
    }

    private fun bindReceivedMessage(holder: ReceivedMessageViewHolder, item: Message) {

        holder.receivedMessage.text = item.text
    }

    private fun bindSentMessage(holder: SentMessageViewHolder, item: Message) {
//        holder.imageButton.setBackgroundResource(item.imgPath)
        holder.sentMessage.text = item.text
    }

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val senderAvatar: View = itemView.findViewById(R.id.senderAvatar)
        val receivedMessage: TextView = itemView.findViewById(R.id.receivedMessage)
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
//        var imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
        val timeOfMessage: TextView = itemView.findViewById(R.id.timeOfMessage)
        val sentMessage: TextView = itemView.findViewById(R.id.sentMessage)
    }
}