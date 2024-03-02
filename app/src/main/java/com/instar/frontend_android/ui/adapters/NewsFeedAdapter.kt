package com.instar.frontend_android.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.Feeds


class NewsFeedAdapter(private val data: List<Feeds>) : RecyclerView.Adapter<NewsFeedAdapter.NewsFeedAViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsFeedAViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.recycler_view_item_newsfeed, parent, false)
        return NewsFeedAViewHolder(view)
    }
    class NewsFeedAViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textView3)
    }
    override fun onBindViewHolder(holder: NewsFeedAViewHolder, position: Int) {
        val item = data[position]
        holder.textView.text = "conmeo"
    }

    override fun getItemCount(): Int = data.size
}
