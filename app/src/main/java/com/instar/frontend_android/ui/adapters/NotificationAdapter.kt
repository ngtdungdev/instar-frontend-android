package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.interfaces.InterfaceUtils

class NotificationAdapter(
    private val context: Context,
    private val notificationList: MutableList<User>,
    private val itemClickListener: InterfaceUtils.OnItemClickListener
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_view_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notificationList = notificationList[position]
        holder.bind(notificationList)
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class NotificationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.imgAvt)
        private val contentTextView: TextView = itemView.findViewById(R.id.tvContent)
        private val timeTextView: TextView = itemView.findViewById(R.id.tvTime)
        private val postImage: ImageView = itemView.findViewById(R.id.imgPost)
        private val followButton: TextView = itemView.findViewById(R.id.btnfollow)
        private val followButton2: TextView = itemView.findViewById(R.id.btnfollow2)

        init {
            followButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = notificationList[position]


                }
            }

            followButton2.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = notificationList[position]



                }
            }
        }

        fun bind(user: User) {

        }
    }
}
