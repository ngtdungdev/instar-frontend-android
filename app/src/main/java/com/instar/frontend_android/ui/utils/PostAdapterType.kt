package com.instar.frontend_android.ui.utils

import com.instar.frontend_android.ui.DTO.Post
import com.instar.frontend_android.ui.DTO.Story
import java.io.Serializable

class PostAdapterType: Serializable {
    var post: Post? = null
    var text: String? = null
    var story: List<Story>? = null
    var type: Int? = null

    constructor(post: Post?, text: String?, story: List<Story>?, type: Int?) {
        this.post = post
        this.text = text
        this.story = story
        this.type = type
    }


    companion object {
        const val VIEW_TYPE_DEFAULT = 0
        const val VIEW_TYPE_SUGGEST = 1
        const val VIEW_TYPE_STORY = 2
    }
}