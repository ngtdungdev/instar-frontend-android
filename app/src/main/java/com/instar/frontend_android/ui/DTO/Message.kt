package com.instar.frontend_android.ui.DTO

import java.time.LocalDateTime

class Message(
    val type: Int,
    val chatId: String,
    val senderId: String,
    val text: String,
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    var id: String? = null

    companion object {
        const val TYPE_AVATAR = 0
        const val TYPE_RECEIVED_MESSAGE = 1
        const val TYPE_SENT_MESSAGE = 2
    }
}