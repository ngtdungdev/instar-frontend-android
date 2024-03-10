package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.ui.DTO.Comment
import okhttp3.MultipartBody
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface PostService {
    companion object {
        const val AUTH_PREFIX = "posts"
    }

    @Multipart
    @POST(AUTH_PREFIX)
    fun createPost(@Part post: Any, @Part files: List<MultipartBody.Part>): Call<ApiResponse<Any>>

    @GET(AUTH_PREFIX)
    fun getAllPosts(): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/get-timeline-posts/{userId}")
    fun getTimelinePosts(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/get-timeline-posts-for-you/{userId}")
    fun getTimelinePostsForYou(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{postId}")
    fun getPost(@Path("postId") postId: String): Call<ApiResponse<Any>>

    @Multipart
    @PUT("$AUTH_PREFIX/{postId}")
    fun updatePost(@Path("postId") postId: String, @Part post: Any, @Part files: List<MultipartBody.Part>): Call<ApiResponse<Any>>

    @DELETE("$AUTH_PREFIX/{userId}/{postId}")
    fun deletePost(@Path("userId") userId: String, @Path("postId") postId: String): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/comment/{postId}")
    fun commentPost(@Path("postId") postId: String, @Body comment: Comment): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/like-comment/{postId}/{commentId}/{userId}")
    fun likeCommentPost(@Path("postId") postId: String, @Path("commentId") commentId: String, @Path("userId") userId: String): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/like/{userId}/{postId}")
    fun likePost(@Path("userId") userId: String, @Path("postId") postId: String): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/saved/{userId}/{postId}")
    fun savePost(@Path("userId") userId: String, @Path("postId") postId: String): Call<ApiResponse<Any>>
}

