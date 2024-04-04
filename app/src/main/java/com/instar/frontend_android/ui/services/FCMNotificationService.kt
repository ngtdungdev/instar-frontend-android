package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.requests.MessageRequest
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
}

