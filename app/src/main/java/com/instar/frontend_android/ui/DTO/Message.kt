package com.instar.frontend_android.ui.DTO

import java.io.Serializable
import java.time.LocalDateTime

class Message: Serializable {
    var id: String? = null
    var content: Any? = null
    var senderId: String? = null
    var chatId: String? = null
    var type: Int? = null
    var createdAt: String = LocalDateTime.now().toString()

    constructor()

    constructor(content: Any, senderId: String, chatId: String, createdAt: LocalDateTime = LocalDateTime.now()) {
        this.content = content
        this.senderId = senderId
        this.chatId = chatId
        this.createdAt = createdAt.toString()
    }

    companion object {
        const val TYPE_AVATAR = 0
        const val TYPE_RECEIVED_MESSAGE = 1
        const val TYPE_SENT_MESSAGE = 2
    }
}