package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.User

class UserResponse {
    @SerializedName("user")
    var user: User? = null

    @SerializedName("followingUsers")
    var followingUsers: List<User>? = null
}