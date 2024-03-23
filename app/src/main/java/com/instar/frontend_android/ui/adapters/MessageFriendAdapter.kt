package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.customviews.ViewEffect

class MessageFriendAdapter(private val data: List<Chat>) : RecyclerView.Adapter<MessageFriendAdapter.MessageFriendViewHolder>() {

    private var listener: OnItemClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageFriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_user, parent, false)
        return MessageFriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageFriendViewHolder, position: Int) {
        val item = data[position]
        ViewEffect.ViewMessage(holder.layout)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
    }

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class MessageFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: View = view.findViewById(R.id.layout)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}