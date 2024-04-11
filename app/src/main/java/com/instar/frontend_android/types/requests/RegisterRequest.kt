package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.ProfilePicture

class RegisterRequest {
    @SerializedName("username")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("fullname")
    var fullname: String? = null

    @SerializedName("email")
    var email: String? = null

    @SerializedName("profilePicture")
    var profilePicture: ProfilePicture? = null
}
