package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class MessageRequest {
    @SerializedName("chatId")
    var chatId: String? = null

    @SerializedName("senderId")
    var senderId: String? = null

    @SerializedName("text")
    var text: String? = null
}