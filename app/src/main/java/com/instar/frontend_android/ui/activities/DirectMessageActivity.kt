package com.instar.frontend_android.ui.activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.gson.Gson
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityDirectMessageBinding
import com.instar.frontend_android.types.requests.MessageRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.DirectMessageAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.MessageService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.awaitResponse
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import com.zegocloud.uikit.prebuilt.call.invite.widget.ZegoSendCallInvitationButton
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.max


class DirectMessageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDirectMessageBinding
    private lateinit var directMessageAvatar: View
    private lateinit var directMessageName: TextView
    private lateinit var directMessageUsername: TextView
    private lateinit var message: EditText
    private lateinit var btnSend: TextView
    private lateinit var iconAvatar : ImageButton
    private lateinit var iconLibrary: ImageButton
    private lateinit var iconMicro: ImageButton
    private lateinit var btnBack: ImageButton
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: DirectMessageAdapter
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var authService: AuthService
    private lateinit var chatService: ChatService
    private lateinit var messageService: MessageService
    private lateinit var userService: UserService
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var userID: String
    private lateinit var chatID: String
    private var user: User? = null
    private lateinit var messagesRef: DatabaseReference
    private val currentChat = Chat()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDirectMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        messageRecyclerView = binding.recyclerView
        directMessageAvatar = binding.directMessageAvatar
        directMessageName = binding.directMessageName
        directMessageUsername = binding.directMessageUsername
        message = binding.message
        btnSend = binding.btnSend
        iconAvatar = binding.iconAvatar
        iconLibrary = binding.iconLibrary
        iconMicro = binding.iconMicro
        btnBack = binding.back

        initServices()

        initVoiceButton()

        initVideoButton()

        btnBack.setOnClickListener {
            super.onBackPressed()
        }
    }

    private fun initServices() {
        authService = ServiceBuilder.buildService(AuthService::class.java, applicationContext)
        chatService = ChatService(applicationContext)
        messageService = MessageService()
        userID = Helpers.getUserId(this).toString()
        chatID = intent.extras?.getString("chatID").toString()
        messagesRef = FirebaseDatabase.getInstance().getReference("messages")

        userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, applicationContext)
        chatService.getChatByMembers(chatID.split("-")) { chat: Chat? ->
            if (chat != null) {
                currentChat.copy(chat)
            } else {
                currentChat.copy(Chat(chatID.split("-")))
            }
            initView()
        }
    }

    private fun initView() {
        // load the direct message details
        lifecycleScope.launch {
            var chatName = currentChat.name
            var chatUsername = "${currentChat.members.size} thành viên"
            var chatAvatarUrl = currentChat.imageUrl ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"
            if (currentChat.members.size == 2) {
                val response = if (currentChat.members[0] == userID)
                    getUserData(currentChat.members[1])
                else
                    getUserData(currentChat.members[0])

                user = response.data?.user

                chatName = "${user?.fullname}"
                chatUsername = "${user?.username}"
                chatAvatarUrl = user?.profilePicture?.url ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"
            }
            Glide.with(applicationContext)
                .load(chatAvatarUrl)
                .placeholder(R.drawable.default_image) // Placeholder image
                .error(R.drawable.default_image) // Image to display if load fails
                .into(directMessageAvatar as ImageView)
            directMessageName.text = chatName
            directMessageUsername.text = chatUsername
        }

        // load all messages of the chat
        messageList = mutableListOf()
        messageService.getMessagesByChatId(chatID) { messages ->
            run {
                messageList.clear()
                val message = Message()
                message.type = Message.TYPE_AVATAR
                messageList.add(message)
                messageList.addAll(messages.toMutableList())
                messageAdapter.notifyItemInserted(max(0, messageList.size - 1))
                messageRecyclerView.scrollToPosition(max(0, messageList.size - 1))
            }
        }
        messageAdapter = DirectMessageAdapter(this, lifecycleScope, messageList, currentChat)
        messageAdapter.notifyItemInserted(max(0, messageList.size - 1))
        val layoutManager = LinearLayoutManager(this)
        messageRecyclerView.layoutManager = layoutManager
        messageRecyclerView.adapter = messageAdapter
        messageRecyclerView.scrollToPosition(max(0, messageList.size - 1))
        messageRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(-1)) {
//                    loadMoreData()
                }
            }
        })

        message.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (message.text.toString().isEmpty()) {
                    iconMicro.visibility = View.VISIBLE
                    iconLibrary.visibility = View.VISIBLE
                    iconAvatar.visibility = View.VISIBLE
                    btnSend.visibility = View.GONE
                    message.width = 300
                } else {
                    iconMicro.visibility = View.GONE
                    iconLibrary.visibility = View.GONE
                    iconAvatar.visibility = View.GONE
                    btnSend.visibility = View.VISIBLE
                    message.width = ViewGroup.LayoutParams.MATCH_PARENT
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        btnSend.setOnClickListener {
            val messageText = message.text.toString().trim()
            sendMessage(messageText)
            message.text.clear();
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    private fun sendMessage(messageText: String) {
        if (messageList.size <= 1) {
            chatService.createNewChat(currentChat)
        }
        val message = Message(messageText, userID, chatID)
        messageService.createNewMessage(message) // push to Firebase
        val messageRequest = MessageRequest().apply {
            text = messageText
            senderId = userID
            chatId = chatID
        }
        fcmNotificationService.sendChatNotification(messageRequest).handleResponse(
            onSuccess = { println("Successfully sent the chat notification.") },
            onError = { println("Error while sending chat notification.") }
        )
    }

    private fun initVideoButton() {
        val newVideoCall = findViewById<ZegoSendCallInvitationButton>(R.id.new_video_call)
        newVideoCall.setIsVideoCall(true)

        newVideoCall.resourceID = "zego_data"
        newVideoCall.setOnClickListener { v: View? ->
            if (user != null) {
                val targetUserID = user!!.id
                val split =
                    targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val users: MutableList<ZegoUIKitUser> = ArrayList()
                for (userID: String in split) {
                    val userName = userID + "_name"
                    users.add(ZegoUIKitUser(userID, userName))
                }
                newVideoCall.setInvitees(users)
            }
        }
    }

    private fun initVoiceButton() {
        val newVoiceCall = findViewById<ZegoSendCallInvitationButton>(R.id.new_voice_call)
        newVoiceCall.setIsVideoCall(false)

        newVoiceCall.resourceID = "zego_data"
        newVoiceCall.setOnClickListener { v: View? ->
            if (user != null) {
                val targetUserID = user!!.id
                val split =
                    targetUserID.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                        .toTypedArray()
                val users: MutableList<ZegoUIKitUser> =
                    ArrayList()
                for (userID: String in split) {
                    val userName = userID + "_name"
                    users.add(ZegoUIKitUser(userID, userName))
                }
                newVoiceCall.setInvitees(users)
            }
        }
    }

    private suspend fun getUserData(userId: String): ApiResponse<UserResponse> {
        return withContext(Dispatchers.IO) {
            userService.getUser(userId).awaitResponse()
        }
    }


}