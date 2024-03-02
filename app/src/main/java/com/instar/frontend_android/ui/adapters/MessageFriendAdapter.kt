package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Feeds
import com.instar.frontend_android.ui.DTO.Friends

class MessageFriendAdapter(private val data: List<Friends>) : RecyclerView.Adapter<MessageFriendAdapter.MyViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_item, parent, false)
        return MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = data[position]
    }

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
    }
    override fun getItemCount(): Int = data.size
}