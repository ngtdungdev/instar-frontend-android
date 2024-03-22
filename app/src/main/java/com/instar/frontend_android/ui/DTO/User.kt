package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class User(
    var id: String,
    var fullname: String,
    var username: String,
    var email: String,
    var desc: String,
    var followers: List<String>,
    var followings: List<String>,
    var notifications: List<Notification>,
    var password: String? = null,
    var profilePicture: ProfilePicture? = null,
    var savedPosts: List<String>,
    var stories: List<Story>,
    var verifyCode: String,
    var createdAt: String,
    var updatedAt: String
) : Serializable