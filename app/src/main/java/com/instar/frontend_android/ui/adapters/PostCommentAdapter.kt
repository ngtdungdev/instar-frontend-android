package com.instar.frontend_android.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.RecyclerViewItemAvatarBinding
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.CommentWithReply
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.fragments.CommentBottomSheetDialogFragment
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.NotificationService
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCommentAdapter(private val context: Context, private val data: MutableList<CommentWithReply>, private val lifecycleScope: LifecycleCoroutineScope, private val postId: String, private val userId: String) : RecyclerView.Adapter<PostCommentAdapter.ViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var notificationService: NotificationService


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: View = view.findViewById(R.id.avatar)
        val nameAndDay: TextView = view.findViewById(R.id.nameAndDay)
        val text: TextView = view.findViewById(R.id.text)
        val btnReply: TextView = view.findViewById(R.id.btnReply)
        val btnSeeMore: View = view.findViewById(R.id.btnSeeMore)
        val commentReplyRecyclerView: RecyclerView = view.findViewById(R.id.commentReplyRecyclerView)
        val like: ImageView = view.findViewById(R.id.btnLike)
        val textTotalLike: TextView = view.findViewById(R.id.textTotalLike)
        val totalReplyComment: TextView = view.findViewById(R.id.totalReplyComment)
        var isLiked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        postService = ServiceBuilder.buildService(PostService::class.java, parent.context)
        notificationService = ServiceBuilder.buildService(NotificationService::class.java, parent.context)
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, parent.context)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_post_comment_item, parent, false))
    }

    suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val commentWithReply = data[position]
        var user: User? = null

        lifecycleScope.launch {
            try {
                val response = getUserData(commentWithReply.comment.userId)

                val avatarBinding = RecyclerViewItemAvatarBinding.bind(holder.avatar)

                avatarBinding.hasStory.visibility = View.GONE

                if (response.data != null) {
                    user = response.data.user
                    if (user != null) {
                        if (user!!.stories.isEmpty()) {
                            avatarBinding.hasStory.visibility = View.GONE
                        } else {
                            avatarBinding.hasStory.visibility = View.VISIBLE
                        }

                        withContext(Dispatchers.Main) {
                            // Load avatar image using Glide
                            Glide.with(context)
                                .load(user!!.profilePicture?.url)
                                .placeholder(R.drawable.default_image) // Placeholder image
                                .error(R.drawable.default_image) // Image to display if load fails
                                .into(avatarBinding.url)
                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        // Load avatar image using Glide
                        Glide.with(context)
                            .load("https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg")
                            .placeholder(R.drawable.default_image) // Placeholder image
                            .error(R.drawable.default_image) // Image to display if load fails
                            .into(avatarBinding.url)
                    }

                    Log.d("tag", "Unsuccessful response: ${response.status}")
                }
            } catch (e: Exception) {
                // Handle exceptions
                Log.e("tag", "Error: ${e.message}", e)
            }
        }


        holder.nameAndDay.text = commentWithReply.comment.username
        holder.text.text = commentWithReply.comment.content
        holder.textTotalLike.text = commentWithReply.comment.likes.size.toString()

        val postCommentReplyAdapter = PostCommentReplyAdapter(context, commentWithReply.replies, lifecycleScope, postId, userId);

        holder.commentReplyRecyclerView.layoutManager = LinearLayoutManager(context)

        holder.commentReplyRecyclerView.adapter = postCommentReplyAdapter

        holder.btnSeeMore.setOnClickListener {
            if (holder.commentReplyRecyclerView.visibility == View.GONE) {
                holder.totalReplyComment.text = "Ẩn bớt các câu trả lời khác"
                holder.commentReplyRecyclerView.visibility = View.VISIBLE
            } else {
                holder.commentReplyRecyclerView.visibility = View.GONE
                holder.totalReplyComment.text = "Xem ${commentWithReply.replies.size} câu trả lời khác"
            }
        }

        if (commentWithReply.replies.size > 0) {
            holder.totalReplyComment.text = "Xem ${commentWithReply.replies.size} câu trả lời khác"
            holder.btnSeeMore.visibility = View.VISIBLE
        } else {
            holder.btnSeeMore.visibility = View.GONE
        }

        holder.btnReply.setOnClickListener {
            user?.let { it1 -> CommentBottomSheetDialogFragment.mention(commentWithReply.comment, it1) }
        }

        holder.isLiked = commentWithReply.comment.likes.contains(userId)

        if (holder.isLiked) {
            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
        } else {
            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
        }

        holder.like.setOnClickListener {
            if (holder.isLiked) {
                holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
            } else {
                holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
            }

            commentWithReply.comment?.id?.let { it1 ->
                postService.likeCommentPost(postId, it1, userId).handleResponse(
                    onSuccess = { response ->
                        if (holder.isLiked) {
                            commentWithReply.comment.likes.remove(userId)
                            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
                        } else {
                            commentWithReply.comment.likes.add(userId)
                            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)

                            val notificationRequest = NotificationRequest(postId, it1, userId, commentWithReply.comment.userId, "like-comment")

                            notificationService.createNotification(userId, notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the comment notification.") },
                                onError = { println("Error while sending comment notification.") }
                            )

                            fcmNotificationService.sendLikeCommentNotification(notificationRequest).handleResponse(
                                onSuccess = { println("Successfully sent the comment notification.") },
                                onError = { println("Error while sending comment notification.") }
                            )
                        }
                        holder.isLiked = !holder.isLiked
                        holder.textTotalLike.text = commentWithReply.comment.likes.size.toString()
                    },
                    onError = { error ->
                        // Handle error
                        val message = error.message;
                        Log.e("ServiceBuilder", "Error: $message - ${error.status}")
                    }
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }
}