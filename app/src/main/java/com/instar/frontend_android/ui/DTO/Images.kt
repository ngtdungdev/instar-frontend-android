package com.instar.frontend_android.ui.DTO
class Images(var type: Int, var name: String, var imgPath: Int) {

    companion object {
        const val TYPE_PERSONAL_AVATAR = 0
        const val TYPE_FRIEND_AVATAR = 1
    }
}
