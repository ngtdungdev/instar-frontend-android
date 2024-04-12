package com.instar.frontend_android.ui.adapters
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.R
import com.instar.frontend_android.interfaces.InterfaceUtils
class SearchAdapter(private val context: Context, private val userList: MutableList<User>, private val itemClickListener: InterfaceUtils.OnItemClickListener) : RecyclerView.Adapter<SearchAdapter.UserViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.recycle_view_item_search, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = userList[position]
        holder.bind(user)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val avatar: ImageView = itemView.findViewById(R.id.imgAvt)
        private val userNameTextView: TextView = itemView.findViewById(R.id.tvUseName)
        private val fullNameTextView: TextView = itemView.findViewById(R.id.tvTen)
        private val followersTextView: TextView = itemView.findViewById(R.id.tvSLNguoiTheoDoi)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val user = userList[position]
                    itemClickListener.onItemClick(user)
                }
            }
        }

        fun bind(user: User) {
            userNameTextView.text = user.username
            fullNameTextView.text = user.fullname
            followersTextView.text = "${user?.followers?.size.toString()} người theo dõi"
            Glide.with(context)
                .load(user.profilePicture?.url)
                .placeholder(R.drawable.default_image) // Placeholder image
                .error(R.drawable.default_image) // Image to display if load fails
                .into(avatar)
        }
    }
}
