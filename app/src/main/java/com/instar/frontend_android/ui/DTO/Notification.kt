package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Notification: Serializable {
    var id: String? = null
    var type: String? = null
    var postId: String? = null
    var commentId: String? = null
    var fromUserId: String? = null
    var message: String? = null
    var createdAt: String? = null
}