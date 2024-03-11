package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class ImageAndVideoInternalMemory (val id: String, val uri: String, val filePath: String, val time: String,  val duration: String, val type: Int){
    companion object {
        const val TYPE_IMAGE = 0
        const val TYPE_VIDEO = 1
    }
}