package com.instar.frontend_android.ui.DTO

class StoryTimeline {
    var stories: List<Story>? = null
    var userId: String? = null

    constructor(stories: List<Story>?, userId: String?) {
        this.stories = stories
        this.userId = userId
    }
}