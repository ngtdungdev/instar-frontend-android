package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Post(
    var id: String,
    var desc: String,
    var feeling: String,
    var fileUploads: MutableList<ProfilePicture>,
    var likes: MutableList<String>,
    var tagUsers: MutableList<String>,
    var userId: String,
    var comments: MutableList<Comment>,
    var createdAt: String,
    var updatedAt: String,
): Serializable
