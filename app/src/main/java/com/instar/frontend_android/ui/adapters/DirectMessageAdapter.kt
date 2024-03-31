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
import com.instar.frontend_android.ui.services.MessageService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class DirectMessageAdapter(
    private val applicationContext: Context,
    private val lifeCycle: LifecycleCoroutineScope,
    private val data: List<Message>,
    private val chat: Chat
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var itemAvatar: View
    private lateinit var messageService: MessageService
    private val userService: UserService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
    private val userId: String = Helpers.getUserId(applicationContext).toString()
    private var chatAvatarUrl: String = chat.imageUrl ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = when (viewType) {
            Message.TYPE_AVATAR -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_avatar_item, parent, false)
            Message.TYPE_SENT_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_right_item, parent, false)
            Message.TYPE_RECEIVED_MESSAGE -> LayoutInflater.from(parent.context).inflate(R.layout.adapter_message_left_item, parent, false)
            else -> null
        }
        return when (viewType) {
            Message.TYPE_AVATAR -> AvatarViewHolder(view!!)
            Message.TYPE_SENT_MESSAGE -> SentMessageViewHolder(view!!)
            Message.TYPE_RECEIVED_MESSAGE -> ReceivedMessageViewHolder(view!!)
            else -> throw IllegalArgumentException("Error")
        }
    }

    override fun getItemViewType(position: Int): Int {
        val message = data[position]
        if (position == 0) {
            message.type = Message.TYPE_AVATAR
        } else if (message.senderId == userId) {
            message.type = Message.TYPE_SENT_MESSAGE
        } else {
            message.type = Message.TYPE_RECEIVED_MESSAGE
        }
        return message.type!!
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item: Message = data[position]
        when (holder) {
            is AvatarViewHolder -> {
                bindAvatar(holder, item)
            }

            is ReceivedMessageViewHolder -> {
                holder.imageAvatar.visibility = if (shouldShowAvatar(position)) View.VISIBLE else View.INVISIBLE
                holder.timeOfMessage.visibility = if (shouldShowTime(position)) View.VISIBLE else View.GONE
                // TODO: open profile when click on senderAvatar
                bindReceivedMessage(holder, item)
            }

            is SentMessageViewHolder -> {
                holder.timeOfMessage.visibility = if (shouldShowTime(position)) View.VISIBLE else View.GONE
                bindSentMessage(holder, item)
            }
        }
    }

    override fun getItemCount(): Int = data.size

    private fun shouldShowAvatar(position: Int): Boolean {
        if (position == data.size - 1)
            return true
        if (shouldShowTime(position + 1))
            return true
        return data[position].type != data[position + 1].type
    }

    private fun shouldShowTime(position: Int): Boolean {
        if (position == 1)
            return true
        val currentMessageTime = LocalDateTime.parse(data[position].createdAt)
        val previousMessageTime = LocalDateTime.parse(data[position - 1].createdAt)
        // should show the time if the time between them is more than 5 minutes
        return currentMessageTime.minusMinutes(5).isBefore(previousMessageTime)
    }

    private suspend fun getUserData(userId: String): User? {
        val response = withContext(Dispatchers.IO) {
            return@withContext userService.getUser(userId).awaitResponse()
        }
        return response.data?.user
    }

    private fun bindAvatar(holder: AvatarViewHolder, item: Message) {
        lifeCycle.launch {
            var user: User? = null
            if (chat.members.size == 2) {
                user = if (chat.members[0] == userId)
                    getUserData(chat.members[1])
                else
                    getUserData(chat.members[0])
            }
            var chatAvatarUrl: String? = chat.imageUrl // custom avatar
            var chatName: String? = chat.name // custom name

            if (user != null && chatAvatarUrl == null) { // 2 people && no custom avatar
                chatAvatarUrl = user.profilePicture?.url ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"
            } else if (chatAvatarUrl == null) { // many people && no custom avatar
                chatAvatarUrl = "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg" // TODO: change to default group avatar
            }
            Glide.with(applicationContext)
                .load(chatAvatarUrl)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(holder.directMessageAvatar as ImageView)


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
            holder.directMessageName.text = chatName


            val chatUsername: String = if (chat.members.size == 2) {
                "Instar • ${user?.username}"
            } else {
                "Instar • ${chat.members.size} thành viên"
            }
            holder.directMessageUsername.text = chatUsername
        }
    }

    private fun bindReceivedMessage(holder: ReceivedMessageViewHolder, item: Message) {
        lifeCycle.launch {
            val avatarUrl: String? = if (chat.members.size == 2) {
                chatAvatarUrl
            } else {
                val user = getUserData(item.senderId.toString())
                user?.profilePicture?.url
            }
            Glide.with(applicationContext)
                .load(avatarUrl)
                .placeholder(R.drawable.default_image)
                .error(R.drawable.default_image)
                .into(holder.senderAvatar as ImageView)
            holder.receivedMessage.text = item.content.toString()

            val time = LocalDateTime.parse(item.createdAt)
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))
            holder.timeOfMessage.text = time
        }
    }

    private fun bindSentMessage(holder: SentMessageViewHolder, item: Message) {
        val time = LocalDateTime.parse(item.createdAt)
            .format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss"))
        holder.timeOfMessage.text = time
        holder.sentMessage.text = item.content.toString()
    }

    class AvatarViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val directMessageAvatar: View = itemView.findViewById(R.id.directMessageAvatarItem)
        val directMessageName: TextView = itemView.findViewById(R.id.directMessageNameItem)
        val directMessageUsername: TextView = itemView.findViewById(R.id.directMessageUsernameItem)
    }

    class ReceivedMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeOfMessage: TextView = itemView.findViewById(R.id.timeOfReceivedMessage)
        val imageAvatar: View = itemView.findViewById(R.id.imageAvatar)
        val senderAvatar: View = itemView.findViewById(R.id.senderAvatar)
        val receivedMessage: TextView = itemView.findViewById(R.id.receivedMessage)
    }

    class SentMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val timeOfMessage: TextView = itemView.findViewById(R.id.timeOfSentMessage)
        val sentMessage: TextView = itemView.findViewById(R.id.sentMessage)
    }
}