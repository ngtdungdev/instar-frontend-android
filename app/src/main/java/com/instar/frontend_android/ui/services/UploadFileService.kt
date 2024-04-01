package com.instar.frontend_android.ui.services

import com.instar.frontend_android.types.responses.ApiResponse
import com.instar.frontend_android.ui.DTO.ProfilePicture
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface UploadFileService {
    companion object {
        const val AUTH_PREFIX = "uploads"
    }

    @Multipart
    @POST(AUTH_PREFIX)
    fun uploadFiles(
        @Part files: List<MultipartBody.Part>
    ): Call<ApiResponse<List<ProfilePicture>>>

    @Multipart
    @POST("$AUTH_PREFIX/vision")
    fun checkVision(
        @Part files: List<MultipartBody.Part>
    ): Call<ApiResponse<Any>>

    @POST("$AUTH_PREFIX/{filename}")
    fun delete(
        @Path("filename") filename: String
    ): Call<ApiResponse<Any>>
}

