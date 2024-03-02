package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Messages

class DirectMessageAdapter(private val data: List<Messages>) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            Messages.TYPE_AVATAR -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_avatar_item, parent, false)
            Messages.TYPE_SENT_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_right_item, parent, false)
            Messages.TYPE_RECEIVED_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_left_item, parent, false)
            else -> null
        }
        return when (viewType) {
            Messages.TYPE_AVATAR -> Avatar(view!!)
            Messages.TYPE_SENT_MESSAGE -> SentMessage(view!!)
            Messages.TYPE_RECEIVED_MESSAGE -> ReceivedMessage(view!!)
            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // Logic to bind data to holder
    }

    class Avatar(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
    }

    class ReceivedMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
    }

    class SentMessage(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageButton: ImageButton = itemView.findViewById(R.id.imageButton)
    }

    override fun getItemCount(): Int = data.size
}