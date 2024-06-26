package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.Comment
import com.instar.frontend_android.ui.DTO.Post

class PostResponse {
    @SerializedName("timelinePosts")
    var timelinePosts: ArrayList<Post>? = null

    @SerializedName("timelineYourPosts")
    var timelineYourPosts: ArrayList<Post>? = null

    @SerializedName("posts")
    var posts: MutableList<Post>? = null

    @SerializedName("post")
    var post: Post? = null

    @SerializedName("comment")
    var comment: Comment? = null
}