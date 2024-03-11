package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.types.responses.UserResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface UserService {
    companion object {
        const val AUTH_PREFIX = "users"
    }

    @POST(AUTH_PREFIX)
    fun createUser(@Body userRequestDTO: Any): Call<ApiResponse<Any>>

    @PUT("$AUTH_PREFIX/{userId}")
    fun updateUser(@Path("userId") userId: String, @Body userRequestDTO: Any): Call<ApiResponse<Any>>

    @DELETE("$AUTH_PREFIX/{userId}")
    fun deleteUser(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET(AUTH_PREFIX)
    fun getUsers(): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{userId}")
    fun getUser(@Path("userId") userId: String): Call<ApiResponse<UserResponse>>

    @GET("$AUTH_PREFIX/email/{email}")
    fun getByEmail(@Path("email") email: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/saved-posts/{userId}")
    fun getSavedPostsByUserId(@Path("userId") userId: String): Call<ApiResponse<Any>>
}

