package com.instar.frontend_android.types.requests

class PostRequest {
    private val userId: String? = null

    private val desc: String? = null

    private val feeling: String? = null

    private val tagUser: List<String>? = null

    constructor(userId: String, desc: String, feeling: String, tagUser: List<String>)
    constructor(userId: String, desc: String)
}