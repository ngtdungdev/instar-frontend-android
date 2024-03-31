package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Story(
    var id: String,
    var userId: String,
    var fileUploads: ProfilePicture,
    var createdAt: String,
    var expiredAt: String,
) : Serializable