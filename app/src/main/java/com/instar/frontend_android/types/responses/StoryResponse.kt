package com.instar.frontend_android.types.responses

import com.google.gson.annotations.SerializedName
import com.instar.frontend_android.ui.DTO.Story
import com.instar.frontend_android.ui.DTO.StoryTimeline

class StoryResponse {
    @SerializedName("timelineStories")
    var timelineStories: ArrayList<StoryTimeline>? = null
    @SerializedName("stories")
    var stories: ArrayList<StoryTimeline>? = null
}