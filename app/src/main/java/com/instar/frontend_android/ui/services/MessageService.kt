package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface MessageService {
    companion object {
        const val AUTH_PREFIX = "messages"
    }

    @POST("$AUTH_PREFIX")
    fun createMessage(@Body message: Any): Call<ApiResponse<Any>>

    @GET("${AUTH_PREFIX}/all/{chatId}")
    fun getAllByChatId(@Path("chatId") chatId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{messageId}")
    fun getMessage(@Path("messageId") messageId: String): Call<ApiResponse<Any>>

    @DELETE("$AUTH_PREFIX/{messageId}")
    fun deleteMessage(@Path("messageId") messageId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{messageId}")
    fun updateMessage(@Path("messageId") messageId: String, @Body message: Any): Call<ApiResponse<Any>>
}

