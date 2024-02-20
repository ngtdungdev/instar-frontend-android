package com.instar.frontend_android.types.responses

data class ApiResponse<T>(
    val `data`: T,
    val message: String,
    val status: String
)