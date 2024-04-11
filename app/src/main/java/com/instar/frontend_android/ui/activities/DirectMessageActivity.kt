package com.instar.frontend_android.ui.activities

import android.Manifest
import android.annotation.SuppressLint
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
import com.instar.frontend_android.R
import com.instar.frontend_android.databinding.ActivityDirectMessageBinding
import com.instar.frontend_android.types.requests.MessageRequest
import com.instar.frontend_android.ui.DTO.Chat
import com.instar.frontend_android.ui.DTO.Message
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.adapters.DirectMessageAdapter
import com.instar.frontend_android.ui.services.AuthService
import com.instar.frontend_android.ui.services.ChatService
import com.instar.frontend_android.ui.services.FCMNotificationService
import com.instar.frontend_android.ui.services.MessageService
import com.instar.frontend_android.ui.services.ServiceBuilder
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.services.UserService
import com.instar.frontend_android.ui.utils.Helpers
import com.permissionx.guolindev.PermissionX
import com.permissionx.guolindev.callback.ExplainReasonCallback
import com.permissionx.guolindev.callback.RequestCallback
import com.zegocloud.uikit.plugin.invitation.ZegoInvitationType
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallConfig
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.event.CallEndListener
import com.zegocloud.uikit.prebuilt.call.event.ErrorEventsListener
import com.zegocloud.uikit.prebuilt.call.event.SignalPluginConnectListener
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoCallInvitationData
import com.zegocloud.uikit.prebuilt.call.invite.internal.ZegoUIKitPrebuiltCallConfigProvider
import com.zegocloud.uikit.service.express.IExpressEngineEventHandler
import im.zego.zegoexpress.constants.ZegoRoomStateChangedReason
import org.json.JSONObject
import timber.log.Timber
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
    private lateinit var messageList: MutableList<Message>
    private lateinit var messageAdapter: DirectMessageAdapter
    private lateinit var messageRecyclerView: RecyclerView
    private lateinit var authService: AuthService
    private lateinit var messageService: MessageService
    private lateinit var userService: UserService
    private lateinit var fcmNotificationService: FCMNotificationService
    private lateinit var userID: String
    private lateinit var chatID: String
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

        initServices()

        PermissionX.init(this).permissions(Manifest.permission.SYSTEM_ALERT_WINDOW)
            .onExplainRequestReason(ExplainReasonCallback { scope, deniedList ->
                val message =
                    "We need your consent for the following permissions in order to use the offline call function properly"
                scope.showRequestReasonDialog(deniedList, message, "Allow", "Deny")
            }).request(RequestCallback { allGranted, grantedList, deniedList -> })
    }

    private fun initServices() {
        authService = ServiceBuilder.buildService(AuthService::class.java, applicationContext)
        messageService = MessageService()
        userID = Helpers.getUserId(this).toString()
        chatID = intent.extras?.getString("chatID").toString()
        messagesRef = FirebaseDatabase.getInstance().getReference("messages")

        userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
        fcmNotificationService = ServiceBuilder.buildService(FCMNotificationService::class.java, applicationContext)
        ChatService(applicationContext)
            .getChatByMembers(chatID.split("-")) { chat: Chat? ->
                if (chat != null) {
                    currentChat.copy(chat)
                    initView()
                }
            }
    }

    private fun initView() {
        // load the direct message details
//        lifecycleScope.launch {
            var chatName = currentChat.name
            var chatUsername = "${currentChat.members.size} thành viên"
            var chatAvatarUrl = currentChat.imageUrl ?: "https://res.cloudinary.com/dt4pt2kyl/image/upload/v1687772432/social/qvcog6uqkqfjnp7h5vo2.jpg"
            if (currentChat.members.size == 2) {
                val user = if (currentChat.members[0] == userID)
                    getUserData(currentChat.members[1])
                else
                    getUserData(currentChat.members[0])
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
//        }

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
                    loadMoreData()
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
            val messageText = message.text.toString()
            sendMessage(messageText)
            message.text.clear();
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right)
    }

    fun loadMoreData() {

    }

    private fun sendMessage(messageText: String) {
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

    private fun getUserData(userId: String): User? {
//        val response = withContext(Dispatchers.IO) {
//            return@withContext userService.getUser(userId).awaitResponse()
//        }
//        return response.data?.user
        var user: User? = null
        userService.getUser(userId).handleResponse(
            onSuccess = { response -> user = response.data?.user },
            onError = {}
        )
        return user
    }

    fun getConfig(invitationData: ZegoCallInvitationData): ZegoUIKitPrebuiltCallConfig {
        val isVideoCall = invitationData.type == ZegoInvitationType.VIDEO_CALL.value
        val isGroupCall = invitationData.invitees.size > 1
        val callConfig: ZegoUIKitPrebuiltCallConfig
        callConfig = if (isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVideoCall()
        } else if (!isVideoCall && isGroupCall) {
            ZegoUIKitPrebuiltCallConfig.groupVoiceCall()
        } else if (!isVideoCall) {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVoiceCall()
        } else {
            ZegoUIKitPrebuiltCallConfig.oneOnOneVideoCall()
        }
        return callConfig
    }

    @SuppressLint("BinaryOperationInTimber")
    private fun videoCallServices(userID: String, username: String) {
        val appID: Long = 1574470634 // your App ID of Zoge Cloud
        val appSign = "a5d41bb837834dd85b9c4b7bb00f15e20d11f2f2d77012957dc7839b76a30331"
        val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()

        callInvitationConfig.provider =
            ZegoUIKitPrebuiltCallConfigProvider { invitationData -> getConfig(invitationData) }

        ZegoUIKitPrebuiltCallService.events.errorEventsListener =
            ErrorEventsListener { errorCode, message -> Timber.d("onError() called with: errorCode = [$errorCode], message = [$message]") }
        ZegoUIKitPrebuiltCallService.events.invitationEvents.pluginConnectListener =
            SignalPluginConnectListener { state, event, extendedData ->
                Timber.d(
                    "onSignalPluginConnectionStateChanged() called with: state = [" + state + "], event = [" + event
                            + "], extendedData = [" + extendedData + "]"
                )
            }

        ZegoUIKitPrebuiltCallService.init(
            getApplication(), appID, appSign, userID, username,
            callInvitationConfig
        )

        ZegoUIKitPrebuiltCallService.events.callEvents.callEndListener =
            CallEndListener { callEndReason, jsonObject ->
                Timber.d(
                    "onCallEnd() called with: callEndReason = [" + callEndReason + "], jsonObject = [" + jsonObject
                            + "]"
                )
            }

        ZegoUIKitPrebuiltCallService.events.callEvents.setExpressEngineEventHandler(
            object : IExpressEngineEventHandler() {
                override fun onRoomStateChanged(
                    roomID: String, reason: ZegoRoomStateChangedReason, errorCode: Int,
                    extendedData: JSONObject
                ) {
                    Timber.d(
                        "onRoomStateChanged() called with: roomID = [" + roomID + "], reason = [" + reason
                                + "], errorCode = [" + errorCode + "], extendedData = [" + extendedData + "]"
                    )
                }
            })
    }
}