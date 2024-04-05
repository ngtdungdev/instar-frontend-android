package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.requests.MessageRequest
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.types.responses.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface FCMNotificationService {
    companion object {
        const val AUTH_PREFIX = "fcm"
    }

    @POST("$AUTH_PREFIX/chat-notification")
    fun sendChatNotification(@Body messageRequest: MessageRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/follow-notification")
    fun sendFollowNotification(@Body notificationRequest: NotificationRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/add-comment-notification")
    fun sendAddCommentNotification(@Body notificationRequest: NotificationRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/like-reply-comment-notification")
    fun sendReplyCommentNotification(@Body notificationRequest: NotificationRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/like-post-notification")
    fun sendLikePostNotification(@Body notificationRequest: NotificationRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/like-comment-notification")
    fun sendLikeCommentNotification(@Body notificationRequest: NotificationRequest): Call<ApiResponse<Any>>
}
