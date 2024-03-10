package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.AuthResponse
import com.instar.frontend_android.types.responses.UserResponse
import com.instar.frontend_android.ui.DTO.Chat
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ChatService {
    companion object {
        const val AUTH_PREFIX = "chats"
    }

    @POST("$AUTH_PREFIX")
    fun createChat(@Body chat: Chat): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/user/{userId}")
    fun getChatsByUserId(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{chatId}")
    fun getChatById(@Path("chatId") chatId: String): Call<ApiResponse<Any>>

    @PUT("$AUTH_PREFIX/{chatId}")
    fun updateChat(@Path("chatId") chatId: String): Call<ApiResponse<Any>>
}

