package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class ImageAndVideo(
    val filePath: String,
    val uri: String,
    val rect: String,
    val duration: String,
    val type: Int): Serializable {
    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1
    }
}