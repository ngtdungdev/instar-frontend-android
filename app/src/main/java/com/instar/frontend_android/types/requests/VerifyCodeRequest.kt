package com.instar.frontend_android.types.requests

import com.google.gson.annotations.SerializedName

class VerifyCodeRequest {
    @SerializedName("email")
    var email: String? = null
}