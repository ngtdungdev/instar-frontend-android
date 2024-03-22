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
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.RecyclerViewItemAvatarBinding
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.services.PostService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PostCommentReplyAdapter(private val context: Context, private val data: MutableList<Comment>, private val lifecycleScope: LifecycleCoroutineScope, private val postId: String, private val userId: String) : RecyclerView.Adapter<PostCommentReplyAdapter.ViewHolder>() {
    private lateinit var userService: UserService
    private lateinit var postService: PostService

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val avatar: View = view.findViewById(R.id.avatar)
        val nameAndDay: TextView = view.findViewById(R.id.nameAndDay)
        val text: TextView = view.findViewById(R.id.text)
        val btnReply: TextView = view.findViewById(R.id.btnReply)
        val like: ImageView = view.findViewById(R.id.btnLike)
        val textTotalLike: TextView = view.findViewById(R.id.textTotalLike)
        var isLiked: Boolean = false
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        userService = ServiceBuilder.buildService(UserService::class.java, parent.context)
        postService = ServiceBuilder.buildService(PostService::class.java, parent.context)
        return ViewHolder(LayoutInflater.from(parent.context).inflate(
            R.layout.recycler_view_post_comment_reply_item, parent, false))
    }

    suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comment = data[position]

        lifecycleScope.launch {
            try {
                val response = getUserData(comment.userId)

                val avatarBinding = RecyclerViewItemAvatarBinding.bind(holder.avatar)

                avatarBinding.hasStory.visibility = View.GONE

                if (response.data != null) {
                    val user = response.data.user
                    if (user != null) {
                        if (user.stories.isEmpty()) {
                            avatarBinding.hasStory.visibility = View.GONE
                        } else {
                            avatarBinding.hasStory.visibility = View.VISIBLE
                        }

                        withContext(Dispatchers.Main) {
                            // Load avatar image using Glide
                            Glide.with(context)
                                .load(user.profilePicture?.url)
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


        holder.nameAndDay.text = comment.username
        holder.text.text = comment.content
        holder.textTotalLike.text = comment.likes.size.toString()

        holder.btnReply.setOnClickListener {

        }

        holder.isLiked = comment.likes.contains(userId)

        holder.like.setOnClickListener {
            if (holder.isLiked) {
                holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
            } else {
                holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
            }
        }

        holder.like.setOnClickListener {
            comment.id?.let { it1 ->
                postService.likeCommentPost(postId, it1, userId).handleResponse(
                    onSuccess = { response ->
                        if (holder.isLiked) {
                            comment.likes.remove(userId)
                            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart)
                        } else {
                            comment.likes.add(userId)
                            holder.like.setBackgroundResource(R.drawable.ic_instagram_icon_heart_full)
                        }
                        holder.isLiked = !holder.isLiked
                        holder.textTotalLike.text = comment.likes.size.toString()
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