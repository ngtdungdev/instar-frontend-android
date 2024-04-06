package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class NotificationRequest {
    @SerializedName("postId")
    var postId: String? = null

    @SerializedName("commentId")
    var commentId: String? = null

    @SerializedName("senderId")
    var senderId: String? = null

    @SerializedName("toUserId")
    var toUserId: String? = null

    @SerializedName("message")
    var message: String? = null

    @SerializedName("type")
    var type: String? = null

    constructor(
        postId: String?,
        commentId: String?,
        senderId: String?,
        toUserId: String?,
        message: String?,
        type: String?
    ) {
        this.postId = postId
        this.commentId = commentId
        this.senderId = senderId
        this.toUserId = toUserId
        this.message = message
        this.type = type
    }
}