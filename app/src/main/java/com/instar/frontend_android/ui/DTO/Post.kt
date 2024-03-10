package com.instar.frontend_android.ui.DTO

class Post(
    var id: String,
    var desc: String,
    var feeling: String,
    var fileUploads: List<ProfilePicture>,
    var likes: List<String>,
    var tagUsers: List<String>,
    var userId: String,
    var comments: List<Comment>,
    var createdAt: String,
    var updatedAt: String,
)
