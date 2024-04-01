package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.Story

class StoryResponse {
    @SerializedName("timelineStories")
    var timelineStories: ArrayList<Story>? = null
}