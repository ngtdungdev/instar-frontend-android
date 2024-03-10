package com.instar.frontend_android.types.responses

import com.instar.frontend_android.ui.DTO.ProfilePicture

class UserResponse {
    var id: String? = null
    var username: String? = null
    var fullname: String? = null
    var email: String? = null
    var profilePicture: ProfilePicture? = null
    var desc: String? = null
    var followers: List<String>? = null
    var followings: List<String>? = null
    var savedPosts: List<Any>? = null
    var stories: List<Any>? = null
    var createdAt: String? = null
    var updatedAt: String? = null
}