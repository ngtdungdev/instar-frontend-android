package com.instar.frontend_android.ui.DTO

import android.graphics.Bitmap
import android.graphics.Rect
import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable

class ImageAndVideo(
    val bitmap: ByteArray?,
    val uri: String,
    val duration: String,
    val type: Int): Serializable {
    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1
    }
}