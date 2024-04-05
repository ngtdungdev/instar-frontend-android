package com.instar.frontend_android.types.requests

class NotificationRequest {
    private val postId: String? = null
    private val commentId: String? = null
    private val senderId: String? = null
    private val toUserId: String? = null
    private val type: String? = null

    constructor(
        postId: String?,
        commentId: String?,
        senderId: String,
        toUserId: String,
        type: String)
}