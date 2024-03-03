package com.instar.frontend_android.ui.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Friends
import com.instar.frontend_android.ui.activities.DirectMessageActivity
import com.instar.frontend_android.ui.fragments.MessengerFragment

class MessageFriendAdapter(private val data: List<Friends>) : RecyclerView.Adapter<MessageFriendAdapter.MessageFriendViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageFriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_item, parent, false)
        return MessageFriendViewHolder(view)
    }
    class MessageFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    }

    interface OnItemClickListener {
        fun onItemClick(position: Int?)
    }

    private var listener: OnItemClickListener? = null

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }
    override fun onBindViewHolder(holder: MessageFriendViewHolder, position: Int) {
        val item = data[position]
        holder.itemView.setOnClickListener{
            if (listener != null) {
                listener!!.onItemClick(position)
            }
        }
    }
    override fun getItemCount(): Int = data.size
}