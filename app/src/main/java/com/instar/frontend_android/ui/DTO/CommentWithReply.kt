package com.instar.frontend_android.ui.DTO

import java.io.Serializable

class CommentWithReply (
    var comment: Comment,
    var replies: MutableList<Comment>,
): Serializable