package com.instar.frontend_android.data.remote.sockets

interface ChatEventListener {
    fun onNewMessage(message: String)
    fun onOpen()
    fun onClosed(code: Int, reason: String)
}