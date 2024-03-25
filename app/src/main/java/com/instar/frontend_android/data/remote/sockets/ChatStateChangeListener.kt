package com.instar.frontend_android.data.remote.sockets

interface ChatStateChangeListener {
    fun onStateChange(state: Socket.State)
}