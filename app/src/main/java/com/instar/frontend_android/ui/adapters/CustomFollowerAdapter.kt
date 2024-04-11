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
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CustomFollowerAdapter(private val context: Context, private var userList: List<String>, private var lifecycleCoroutineScope: LifecycleCoroutineScope) :
    RecyclerView.Adapter<CustomFollowerAdapter.ViewHolder>() {
    private val userService: UserService = ServiceBuilder.buildService(UserService::class.java, context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_follower, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user: String = userList[position]
        lifecycleCoroutineScope.launch {
            try {
                val response = getUserData(user)
                val userFollower = response.data?.user
                userFollower.let { updateUserInformation(holder, it) }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun updateUserInformation(holder: ViewHolder, user: User?) {
        Glide.with(context)
            .load(user?.profilePicture?.url)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(holder.imageAvatar)
        holder.tvUsername.text = user?.username
        holder.tvFullname.text = user?.fullname
        holder.btnRemove.setOnClickListener{
            user?.let { it1 ->
                userService.removeFollower(it1.id).handleResponse(
                    onSuccess = {

                        val position = userList.indexOf(user.id)
                        if (position != -1) { // Đảm bảo mục tồn tại trong danh sách
                            // Loại bỏ mục khỏi danh sách
                            userList = userList.filterIndexed { index, _ -> index != position }
                            notifyItemRemoved(position)
                        }
                    },
                    onError = {

                    }
                )
            }
        }
    }


    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        val tvUsername: TextView = itemView.findViewById(R.id.tvUsername)
        val tvFullname: TextView = itemView.findViewById(R.id.tvFullname)
        val btnRemove: TextView = itemView.findViewById(R.id.btnRemove)
    }
    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }
}
