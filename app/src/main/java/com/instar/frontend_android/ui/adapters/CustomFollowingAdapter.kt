package com.instar.frontend_android.ui.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.R
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomFollowingAdapter(private val context: Context, private var userList: List<String>, private var lifecycleCoroutineScope: LifecycleCoroutineScope) :
    RecyclerView.Adapter<CustomFollowingAdapter.ViewHolder>()  {
    private val userService: UserService = ServiceBuilder.buildService(UserService::class.java, context)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_following, parent, false)
        return ViewHolder(view)
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: CustomFollowingAdapter.ViewHolder, position: Int) {
        val user: String = userList[position]
        lifecycleCoroutineScope.launch {
            try {
                val response = getUserData(user)
                val userFollowing = response.data?.user
                userFollowing.let { updateUserInformation(holder, it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvFullname: TextView = itemView.findViewById(R.id.tvFullname)
        val btnFollow: TextView = itemView.findViewById(R.id.btnFollow)
    }
    private fun updateUserInformation(holder: CustomFollowingAdapter.ViewHolder, user: User?) {
        if (user != null) {
            Log.d("UserFollowing", "User ID: ${user.id}, Username: ${user.username}, Fullname: ${user.fullname}")
            Glide.with(context)
                .load(user.profilePicture?.url)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(holder.imageAvatar)
            holder.tvUsername.text = user.username
            holder.tvFullname.text = user.fullname
        } else {
            // Xử lý trường hợp userFollowing là null ở đây (nếu cần)
        }
    }
    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}