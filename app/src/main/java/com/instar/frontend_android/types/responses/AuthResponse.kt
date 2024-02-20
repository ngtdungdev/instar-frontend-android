package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName

class AuthResponse {
    @SerializedName("access_token")
    var accessToken: String? = null

    @SerializedName("refresh_token")
    var refreshToken: String? = null

    @SerializedName("user")
    var user: String? = null
}