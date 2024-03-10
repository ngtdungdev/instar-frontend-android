package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.ui.DTO.Story
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface StoryService {
    companion object {
        const val AUTH_PREFIX = "stories"
    }

    @DELETE("$AUTH_PREFIX/{userId}/{storyId}")
    fun deleteStory(@Path("userId") userId: String, @Path("storyId") storyId: String): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/{userId}")
    fun createStory(@Path("userId") userId: String, @Body storyDetail: Story): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{userId}/{storyId}")
    fun getStoryByStoryId(@Path("userId") userId: String, @Path("storyId") storyId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/{userId}")
    fun getStoriesByUserId(@Path("userId") userId: String): Call<ApiResponse<Any>>

    @GET("$AUTH_PREFIX/timeline/{userId}")
    fun getStoriesTimelineByUserId(@Path("userId") userId: String): Call<ApiResponse<Any>>
}
