package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Comment (
    var id: String? = null,
    var username: String,
    var content: String,
    var parentId: String? = null,
    var userId: String,
    var likes: MutableList<String>,
    var createdAt: String,
    var updatedAt: String
): Serializable