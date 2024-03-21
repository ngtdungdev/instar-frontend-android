package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Messages (var type: Int, val a: String, val b: String) : Serializable{
    companion object {
        const val TYPE_AVATAR = 0
        const val TYPE_RECEIVED_MESSAGE = 1
        const val TYPE_SENT_MESSAGE = 2
    }
}