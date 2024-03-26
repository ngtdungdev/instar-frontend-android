package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.User

class UserResponse {
    @SerializedName("user")
    var user: User? = null

    @SerializedName("users")
    var users: List<User>? = null

    @SerializedName("followingUsers")
    var followingUsers: List<User>? = null

    @SerializedName("posts")
    var posts: List<Post>? = null
}