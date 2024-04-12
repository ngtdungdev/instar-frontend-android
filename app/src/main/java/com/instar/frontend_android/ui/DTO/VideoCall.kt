package com.instar.frontend_android.ui.DTO

class VideoCall {
    var id: String? = null
    var userId: String? = null
    var status: Status? = null

    constructor()

    constructor(id: String?, userId: String?, status: Status?) {
        this.id = id
        this.userId = userId
        this.status = status
    }


    enum class Status {
        IN_CALL,
        OFFLINE,
        ONLINE
    }
}