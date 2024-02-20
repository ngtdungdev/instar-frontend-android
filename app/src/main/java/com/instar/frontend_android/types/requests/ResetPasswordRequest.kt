package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class ResetPasswordRequest {
    @SerializedName("email")
    var email: String? = null

    @SerializedName("newPassword")
    var newPassword: String? = null

    @SerializedName("verifyCode")
    var verifyCode: String? = null
}