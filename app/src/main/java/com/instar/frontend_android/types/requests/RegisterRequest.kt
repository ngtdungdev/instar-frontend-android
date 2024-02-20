package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class RegisterRequest {
    @SerializedName("ac")
    var username: String? = null

    @SerializedName("password")
    var password: String? = null

    @SerializedName("fullname")
    var fullname: String? = null

    @SerializedName("email")
    var email: String? = null
}
