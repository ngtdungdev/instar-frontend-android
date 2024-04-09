package com.instar.frontend_android.types.requests

class PostRequest {
    var userId: String? = null
    var desc: String? = null
    var feeling: String? = null
    var tagUsers: List<String>? = null

    constructor(userId: String, desc: String, feeling: String, tagUsers: List<String>) {
        this.userId = userId
        this.desc = desc
        this.feeling = feeling
        this.tagUsers = tagUsers
    }

    constructor(userId: String, desc: String) {
        this.userId = userId
        this.desc = desc
    }
}
