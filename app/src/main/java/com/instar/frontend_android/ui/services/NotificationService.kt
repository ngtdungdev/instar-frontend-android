package com.instar.frontend_android.ui.services

import com.google.firebase.database.FirebaseDatabase
import com.instar.frontend_android.types.requests.NotificationRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.NotificationResponse
import com.instar.frontend_android.ui.DTO.Notification
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface NotificationService {
    companion object {
        const val AUTH_PREFIX = "notifications"

        private val database = FirebaseDatabase.getInstance()
        private val notificationTokensRef = database.getReference("notificationTokens")

        fun createNewToken(userId: String, token: String) {
            notificationTokensRef.child(userId).setValue(token)
        }

        fun deleteToken(userId: String) {
            notificationTokensRef.child(userId).removeValue()
        }
    }

    @POST("$AUTH_PREFIX/{userId}")
    fun createNotification(@Path("userId") userId: String, @Body notification: NotificationRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/{userId}/{notificationId}")
    fun updateNotification(@Path("userId") userId: String, @Path("notificationId") notificationId: String, @Body notification: Notification): Call<ApiResponse<Any>>

    @DELETE("$AUTH_PREFIX/{userId}/{notificationId}")
    fun deleteNotification(@Path("userId") userId: String, @Path("notificationId") notificationId: String): Call<ApiResponse<Any>>

    @DELETE("$AUTH_PREFIX/{userId}")
    fun deleteNotifications(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{userId}")
    fun getNotifications(@Path("userId") userId: String): Call<ApiResponse<NotificationResponse>>

    @GET("$AUTH_PREFIX/{userId}/{notificationId}")
    fun getNotification(@Path("userId") userId: String, @Path("notificationId") notificationId: String): Call<ApiResponse<Notification>>
}

