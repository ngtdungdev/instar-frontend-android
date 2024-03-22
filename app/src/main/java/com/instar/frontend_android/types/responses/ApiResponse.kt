package com.instar.frontend_android.types.responses

data class ApiResponse<T>(
    val `data`: T? = null,
    val message: String? = null,
    val status: String? = null,
    val error: String? = null
)
