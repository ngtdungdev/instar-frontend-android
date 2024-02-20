package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class LoginRequest {
    @SerializedName("email")
    var email: String? = null

    @SerializedName("password")
    var password: String? = null
}