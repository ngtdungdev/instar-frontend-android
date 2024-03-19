package com.instar.frontend_android.ui.DTO

class Comment (
    var id: String,
    var username: String,
    var content: String,
    var parentId: String,
    var userId: String,
    var likes: List<String>,
    var createdAt: String,
    var updatedAt: String
)