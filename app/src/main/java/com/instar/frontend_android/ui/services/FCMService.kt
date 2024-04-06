package com.instar.frontend_android.ui.services

import android.content.Context
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.instar.frontend_android.ui.DTO.User
import com.instar.frontend_android.ui.services.ServiceBuilder.handleResponse
import com.instar.frontend_android.ui.utils.Helpers

class FCMService : FirebaseMessagingService {
    private var applicationContext: Context
    private var notificationService: NotificationService
    private var userService: UserService
    private var chatService: ChatService
    private var postService: PostService

    constructor() {
        this.applicationContext = this
        this.notificationService = ServiceBuilder.buildService(NotificationService::class.java, this)
        this.userService = ServiceBuilder.buildService(UserService::class.java, this)
        this.postService = ServiceBuilder.buildService(PostService::class.java, this)
        this.chatService = ChatService(this)
    }

    constructor(applicationContext: Context) {
        this.applicationContext = applicationContext
        this.notificationService = ServiceBuilder.buildService(NotificationService::class.java, applicationContext)
        this.userService = ServiceBuilder.buildService(UserService::class.java, applicationContext)
        this.postService = ServiceBuilder.buildService(PostService::class.java, applicationContext)
        this.chatService = ChatService(applicationContext)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        var notificationTitle: String
        var notificationText: String

        val data = remoteMessage.data
        val userId = data["userId"]
        val type = data["type"]
        when (type) {
            "message" -> {
                val chatId = data["chatId"]
                val message = data["message"]

                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        chatService.getChatByMembers(chatId!!.split("-")) { chat ->
                            if (chat != null) {
                                if (chat.members.size == 2) {
                                    notificationTitle = (chat.name ?: user?.fullname).toString()
                                    notificationText = "$message"
                                } else {
                                    notificationTitle = chat.name ?: chat.members.joinToString(", ")
                                    notificationText = "${user?.fullname}: $message"
                                }
                                NotificationHelper.showNotification(this, notificationTitle, notificationText, 1, data)
                            }
                        }
                    },
                    onError = { error -> println(error) }
                )
            }
            "follow" -> {
                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        NotificationHelper.showNotification(this, "Theo dõi", "${user?.username} đã theo dõi bạn", 1, data, user)
                    },
                    onError = { error -> println(error) }
                )
            }
            "like-post" -> {
                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        NotificationHelper.showNotification(this, "Có người đã thích bài viết của bạn", "${user?.username} đã thích bài viết của bạn", 1, data, user)
                    },
                    onError = { error -> println(error) }
                )
            }
            "like-comment" -> {
                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        NotificationHelper.showNotification(this, "Có người đã thích bình luận của bạn", "${user?.username} đã thích bình luận của bạn", 1, data, user)
                    },
                    onError = { error -> println(error) }
                )
            }
            "add-comment" -> {
                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        NotificationHelper.showNotification(this, "Có người đã bình luận vào bài viết của bạn", "${user?.username} đã bình luận vào bài viết của bạn", 1, data, user)
                    },
                    onError = { error -> println(error) }
                )
            }
            "reply-comment" -> {
                var user: User?
                userService.getUser(userId!!).handleResponse(
                    onSuccess = { response ->
                        user = response.data?.user
                        NotificationHelper.showNotification(this, "Có người đã phản hồi bình luận của bạn", "${user?.username} đã bình luận đã phản hồi bình luận của bạn", 1, data, user)
                    },
                    onError = { error -> println(error) }
                )
            }
            else -> {

            }
        }

    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        println("Refreshed token: $token")
        val userId: String? = Helpers.getUserId(applicationContext)
        if (userId != null) {
            NotificationService.createNewToken(userId, token)
        }
    }

    fun getFirebaseCloudMessagingToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val token = task.result

                println(token)
                val userId: String? = Helpers.getUserId(applicationContext)
                if (userId != null) {
                    NotificationService.createNewToken(userId, token)
                }
            }
        }
    }
}
