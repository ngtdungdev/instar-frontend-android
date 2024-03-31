package com.instar.frontend_android.ui.DTO

import java.io.Serializable
import java.time.LocalDateTime

class Chat : Serializable {
    var id: String? = null
    var name: String? = null
    var imageUrl: String? = null
    var members: List<String> = emptyList()
    var createdAt: String = LocalDateTime.now().toString()

    constructor()

    constructor(members: List<String>, createdAt: LocalDateTime = LocalDateTime.now()) {
        this.members = members
        this.createdAt = createdAt.toString()
    }

    fun copy(chat: Chat) {
        this.id = chat.id
        this.name = chat.name
        this.imageUrl = chat.imageUrl
        this.members = chat.members
        this.createdAt = chat.createdAt
    }
}