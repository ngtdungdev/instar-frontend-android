package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.PostResponse
import com.instar.frontend_android.ui.DTO.Notification
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class NotificationAdapter(
    private val context: Context,
    private val notificationList: MutableList<Notification>,
    private val user: User?,
    private val lifecycleScope: LifecycleCoroutineScope,
) : RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_view_item_notification, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notificationList = notificationList[position]
        userService = ServiceBuilder.buildService(UserService::class.java, context)
        postService = ServiceBuilder.buildService(PostService::class.java, context)
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
        private val btnUnfollow: TextView = itemView.findViewById(R.id.btnUnfollow)

        init {
            followButton.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notification = notificationList[position]
                    lifecycleScope.launch {
                        val response = notification.senderId?.let { it1 -> userService.follow(it1).awaitResponse() }
                        val user = response?.data?.user

                        followButton.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }

                        btnUnfollow.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }
                }
            }

            btnUnfollow.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val notification: Notification = notificationList[position]

                    lifecycleScope.launch {
                        val response = notification.senderId?.let { it1 -> userService.follow(it1).awaitResponse() }
                        val user = response?.data?.user

                        followButton.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                            View.GONE
                        } else {
                            View.VISIBLE
                        }

                        btnUnfollow.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                            View.VISIBLE
                        } else {
                            View.GONE
                        }
                    }

                }
            }
        }

        fun bind(notification: Notification) {
            followButton.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                View.GONE
            } else {
                View.VISIBLE
            }

            btnUnfollow.visibility = if (user?.followings?.contains(notification.senderId) == true) {
                View.VISIBLE
            } else {
                View.GONE
            }

            when (notification.type) {
                "like-post" -> {
                    postImage.visibility = View.VISIBLE
                    btnUnfollow.visibility = View.GONE
                    followButton.visibility = View.GONE

                    lifecycleScope.launch {
                        notification.postId?.let {
                            val response = getPost(it)

                            Glide.with(context)
                                .load(response.data?.post?.fileUploads?.get(0)?.url)
                                .placeholder(R.drawable.default_image) // Placeholder image
                                .error(R.drawable.default_image) // Image to display if load fails
                                .into(avatar)
                        }
                    }
                }
                "like-comment" -> {
                    postImage.visibility = View.GONE
                    btnUnfollow.visibility = View.GONE
                    followButton.visibility = View.GONE
                }
                "add-comment" -> {
                    postImage.visibility = View.GONE
                    btnUnfollow.visibility = View.GONE
                    followButton.visibility = View.GONE

                }
                "reply-comment" -> {
                    postImage.visibility = View.GONE
                    btnUnfollow.visibility = View.GONE
                    followButton.visibility = View.GONE

                }
                "follow" -> {
                    postImage.visibility = View.GONE

                }
            }

            contentTextView.text = notification.message ?: "Đoán xem"
            timeTextView.text = Helpers.convertToTimeAgo(notification.createdAt.toString())

            Glide.with(context)
                .load(user?.profilePicture?.url)
                .placeholder(R.drawable.default_image) // Placeholder image
                .error(R.drawable.default_image) // Image to display if load fails
                .into(avatar)

        }
    }

    private suspend fun getPost(postId: String): ApiResponse<PostResponse> {
        return withContext(Dispatchers.IO) {
            postService.getPost(postId).awaitResponse()
        }
    }
}
