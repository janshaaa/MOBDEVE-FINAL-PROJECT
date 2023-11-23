package com.mobdeve.s12.aiwear.models

import java.util.Date

data class ForumCommentModel(
    var comment_id: String = "",
    var post_id: String = "",
    var content: String = "",
    var created_by: String = "",
    var created_at: Date = Date()
) {

    // Secondary constructor with fewer parameters
    constructor(post_id: String, content: String, created_by: String) :
            this("", post_id, content, created_by, Date())

    // Empty constructor
    constructor() : this("", "", "", "", Date())
}



