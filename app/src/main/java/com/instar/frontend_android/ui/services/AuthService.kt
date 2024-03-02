package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.requests.LoginRequest
import com.instar.frontend_android.types.requests.RegisterRequest
import com.instar.frontend_android.types.requests.ResetPasswordRequest
import com.instar.frontend_android.types.requests.VerifyCodeRequest
import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.AuthResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthService {
    companion object {
        const val AUTH_PREFIX = "auth"
    }

    @POST("$AUTH_PREFIX/login")
    fun login(@Body loginRequest: LoginRequest): Call<ApiResponse<AuthResponse>>

    @POST("$AUTH_PREFIX/register")
    fun register(@Body registerRequest: RegisterRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/refresh-token")
    fun refreshToken(): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/profile")
    fun profile(): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/reset-password")
    fun resetPassword(@Body resetPassword: ResetPasswordRequest): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/verify-code")
    fun verifyCode(@Body verifyCodeRequest: VerifyCodeRequest): Call<ApiResponse<Any>>
}

