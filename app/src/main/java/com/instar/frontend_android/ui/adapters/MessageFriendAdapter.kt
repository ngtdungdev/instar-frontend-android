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
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.customviews.ViewEffect
import com.instar.frontend_android.ui.services.MessageService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MessageFriendAdapter(
    private val applicationContext: Context,
    private val lifeCycle: LifecycleCoroutineScope,
    private val data: List<Chat>
) : RecyclerView.Adapter<MessageFriendAdapter.MessageFriendViewHolder>() {

    private var listener: OnItemClickListener? = null
    private val messageService: MessageService = MessageService()
    private val userService: UserService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
    private val userId: String = Helpers.getUserId(applicationContext).toString()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageFriendViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_user, parent, false)
        return MessageFriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageFriendViewHolder, position: Int) {
        val chat: Chat = data[position]
        ViewEffect.ViewMessage(holder.layout)
        holder.itemView.setOnClickListener {
            listener?.onItemClick(position)
        }
        lifeCycle.launch {
            var user: User? = null
            if (chat.members.size == 2) {
                user = if (chat.members[0] == userId)
                    getUserData(chat.members[1])
                else
                    getUserData(chat.members[0])
            }
            bindChatAvatar(chat, user, holder)
            bindChatName(chat, user, holder)
            bindChatLastMessage(chat, user, holder)
        }
    }

    private fun bindChatAvatar(chat: Chat, user: User?, holder: MessageFriendViewHolder) {
        var chatAvatarUrl: String? = chat.imageUrl // custom avatar

        if (user != null && chatAvatarUrl == null) { // 2 people && no custom avatar
            chatAvatarUrl = user.profilePicture?.url ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"
        } else if (chatAvatarUrl == null) { // many people && no custom avatar
            chatAvatarUrl = "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg" // TODO: change to default group avatar
        }
        Glide.with(applicationContext)
            .load(chatAvatarUrl)
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .into(holder.avatar as ImageView)
    }

    private suspend fun bindChatName(chat: Chat, user: User?, holder: MessageFriendViewHolder) {
        var chatName: String? = chat.name // custom name

        if (user != null && chatName == null) { // 2 people && no custom name
            chatName = user.fullname
        } else if (chatName == null) { // many people && no custom name
            val members = mutableListOf<User?>()
            for (id in chat.members) {
                if (id == userId)
                    continue
                val member = getUserData(id)
                members.add(member)
            }
            chatName = members.joinToString(", ")
        }
        holder.title.text = chatName
    }

    private fun bindChatLastMessage(chat: Chat, user: User?, holder: MessageFriendViewHolder) {

        var lastMessage: Message
        messageService.getMessagesByChatId(chat.members.joinToString("-")) { messages ->
            lifeCycle.launch {
                lastMessage = messages[messages.size - 1]
                val lastMessageSender = if (lastMessage.senderId == userId) {
                    "Bạn: "
                } else {
                    if (user != null) {
                        ""
                    } else {
                        val sender: User? = this@MessageFriendAdapter.getUserData(lastMessage.senderId.toString())
                        "${sender?.fullname}: "
                    }
                }
                val lastMessageContent = if (lastMessage.type.isNullOrEmpty()) {
                    lastMessage.content.toString()
                } else if (lastMessage.type.equals("post")) {
                    if (lastMessageSender.endsWith(": "))
                        "đã gửi một bài viết."
                    else
                        "Đã gửi một bài viết."
                } else {
                    lastMessage.content.toString()
                }
                val chatLastMessage = lastMessageSender + lastMessageContent
                holder.text.text = chatLastMessage
            }
        }
    }

    private suspend fun getUserData(userId: String): User? {
        val response = withContext(Dispatchers.Main) {
            return@withContext userService.getUser(userId).awaitResponse()
        }
        return response.data?.user
    }

    override fun getItemCount(): Int = data.size

    fun setOnItemClickListener(listener: OnItemClickListener) {
        this.listener = listener
    }

    class MessageFriendViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val layout: View = view.findViewById(R.id.layout)
        val avatar: View = view.findViewById(R.id.avatar)
        val title: TextView = view.findViewById(R.id.title)
        val text: TextView = view.findViewById(R.id.text)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}