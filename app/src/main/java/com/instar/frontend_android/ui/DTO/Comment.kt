package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Comment (
    var id: String,
    var username: String,
    var content: String,
    var parentId: String,
    var userId: String,
    var likes: MutableList<String>,
    var createdAt: String,
    var updatedAt: String
): Serializable