package com.instar.frontend_android.types.requests

data class SocketRequest(
    val type: String,
    val data: Any?,
    val sendId: String
)
