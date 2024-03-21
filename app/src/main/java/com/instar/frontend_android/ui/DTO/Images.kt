package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class Images(var type: Int, var name: String, var url: String?) : Serializable{

    companion object {
        const val TYPE_PERSONAL_AVATAR = 0
        const val TYPE_FRIEND_AVATAR = 1
    }
}
