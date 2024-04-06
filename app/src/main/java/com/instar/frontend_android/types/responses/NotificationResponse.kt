package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.Notification

class NotificationResponse {
    @SerializedName("notifications")
    var notificationList: MutableList<Notification>? = null
}